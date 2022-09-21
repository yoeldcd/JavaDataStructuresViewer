package com.yoeld99apps.axegrl.displayer;

import com.yoeld99apps.axegrl.components.*;
import com.yoeld99apps.axegrl.painters.*;
import java.awt.Color;
import java.awt.Component;

import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Displayer extends DShape implements DElement {

    public static int SELECT_ITEMS = Integer.parseInt("00000001", 2);
    public static int CUSTOMIZE_ITEMS = Integer.parseInt("00000010", 2);
    public static int CONFIG_ITEMS = Integer.parseInt("00000010", 2);
    public static int MOVE_ITEMS = Integer.parseInt("00000100", 2);
    public static int EDIT_ITEMS = Integer.parseInt("00001000", 2);
    public static int ADD_ITEMS = Integer.parseInt("00010000", 2);
    public static int DELETE_ITEMS = Integer.parseInt("00100000", 2);
    public static int CONECT_ITEMS = Integer.parseInt("01000000", 2);
    public static int GROUP_ITEMS = Integer.parseInt("01000000", 2);
    public static int ALL_CONTROLLS = Integer.parseInt("11111111", 2);

    // avaliable mouse statuses
    private static final int NO_SELECTED_GROUP = 0xA6267000;
    private static final int POINTING_SHAPE = 0xA6267001;
    private static final int POINTING_LINK = 0xA6267001;
    private static final int DRAGGING_SHAPE = 0xA6267002;
    private static final int SELECTED_SHAPES = 0xA6267003;
    private static final int MAKING_GROUP = 0xA6267004;
    private static final int DRAGGING_GROUP = 0xA6267005;
    private static final int SELECTED_GROUP = 0xA6267006;

    // avaliable item menu commands
    private static final String GLOBAL_MENU_NAME = "GlobalMenu";
    private static final String ITEM_MENU_NAME = "ItemMenu";
    private static final String COMMAND_SELECT_ALL_SHAPES = "spSelctAll";
    private static final String COMMAND_ADD_SHAPE = "addSp";
    private static final String COMMAND_RENAME_SHAPE = "spRenm";
    private static final String COMMAND_CUSTOMIZE_SHAPES_STYLES = "custmSpSts";
    private static final String COMMAND_SELECT_SHAPES_STYLE = "selSStl";
    private static final String COMMAND_ENABLE_SELECTION = "enaS";
    private static final String COMMAND_DISABLE_SELECTION = "disS";
    private static final String COMMAND_LINK_SHAPES = "lnkSps";
    private static final String COMMAND_LOOP_LINK_SHAPES = "lnkLoopSps";
    private static final String COMMAND_STAR_LINK_SHAPES = "lnkStarSps";
    private static final String COMMAND_UNLINK_SHAPES = "unkS";
    private static final String COMMAND_DELETE_SHAPES = "delSps";
    private static final String COMMAND_FIX_GROUP = "fxGrp";
    private static final String COMMAND_TAKE_SCREENSHOT = "tsht";

    // avaliables link menu commands
    private static final String LINK_MENU_NAME = "LinksMenu";
    private static final String COMMAND_RENAME_LINK = "lnkRenm";
    private static final String COMMAND_CUSTOMIZE_LINK = "lnkCtmz";
    private static final String COMMAND_INVERT_LINK = "lnkInvt";
    private static final String COMMAND_DELETE_LINK = "lnkDel";

    // used items lists
    private final ArrayList<DShapeGroup> groups;
    private final ArrayList<DConector> conectors;
    private final ArrayList<DShape> shapes;
    private final ArrayList<DShape> temp;
    private final ArrayList<ShapePainter.Style> shapeStyles;
    private final ArrayList<ConectorPainter.Style> conectorStyles;

    private final DShapeGroup globalGroup;
    private DShapeGroup selectedGroup;

    private DShapeGroup pointedGroup;
    private DShape pointedShape;
    private DConector pointedConector;

    private final ShapePainter.Style disableGroupStyle;
    private final ShapePainter.Style enableGroupStyle;
    private final ShapePainter.Style selectedGroupStyle;

    private final ConectorPainter.Style disableConectorStyle;
    private final ConectorPainter.Style enableConectorStyle;
    private final ConectorPainter.Style selectedConectorStyle;

    private final DListSelectionDialog<ShapePainter.Style> selectionShapeStyleDialog;
    private final DListSelectionDialog<ConectorPainter.Style> selectionConectorStyleDialog;
    private final DShapeStyleEditorDialog shapeStyleDialog;
    private final JDialog conectorStyleDialog;

    private int mouseState;
    private int enabledControlsFlag;
    private final Point lastPoint;
    private final Point initialPoint;

    public Displayer() {
        this(ALL_CONTROLLS);
    }

    public Displayer(int enabledControls) {
        super();

        System.out.println(Integer.toString(enabledControls, 2)+"->"+Integer.toString(enabledControls & SELECT_ITEMS, 2));
        
        // configure layout
        setBackground(Color.LIGHT_GRAY);
        setSize(700, 500);

        groups = new ArrayList<>();
        conectors = new ArrayList<>();
        shapes = new ArrayList<>();

        shapeStyles = new ArrayList<>();
        conectorStyles = new ArrayList<>();
        temp = new ArrayList<>(1);
        temp.add(null);
        
        globalGroup = new DShapeGroup();
        selectedGroup = globalGroup;

        // initializing mouse state control values
        mouseState = NO_SELECTED_GROUP;
        enabledControlsFlag = enabledControls;
        lastPoint = new Point();
        initialPoint = new Point();

        // make customizer options dialogs
        selectionShapeStyleDialog = new DListSelectionDialog(shapeStyles);
        selectionConectorStyleDialog = new DListSelectionDialog(conectorStyles);
        shapeStyleDialog = new DShapeStyleEditorDialog(shapeStyles);
        conectorStyleDialog = null;

        // define default styles
        makeShapeStyles();

        disableGroupStyle = new ShapePainter.Style(ShapePainter.Style.GEOMETRY_BOX, null, Color.GRAY, Color.GRAY, ShapePainter.Style.TEXT_OVER);
        enableGroupStyle = new ShapePainter.Style(ShapePainter.Style.GEOMETRY_BOX, null, Color.YELLOW, Color.YELLOW, ShapePainter.Style.TEXT_OVER);
        selectedGroupStyle = new ShapePainter.Style(ShapePainter.Style.GEOMETRY_BOX, Color.WHITE, Color.BLACK, Color.BLACK, ShapePainter.Style.TEXT_OVER);

        disableConectorStyle = new ConectorPainter.Style(ConectorPainter.Style.POLYLINE, Color.GRAY, Color.decode("0x0000B2"));
        enableConectorStyle = new ConectorPainter.Style(ConectorPainter.Style.POLYLINE, Color.GREEN, Color.decode("0x005500"));
        selectedConectorStyle = new ConectorPainter.Style(ConectorPainter.Style.POLYLINE, Color.YELLOW, Color.decode("0x0AAF00"));

        // enable options menus
        if (enabledControls != 0) {
            makeGlobalMenu(enabledControls);
            makeItemsMenu(enabledControls);
            makeLinksMenu(enabledControls);
            
        }

        // initialize components
        initialize();
    }

    private void makeShapeStyles() {

        ShapePainter.Style style = null;

        // define default selected Style
        style = new ShapePainter.Style(ShapePainter.Style.GEOMETRY_FREE, Color.WHITE, Color.decode("0x0000B2"), Color.YELLOW, ShapePainter.Style.TEXT_CENTER);
        style.setName("selected");
        style.setBorderSize(1.2);
        shapeStyles.add(style);
        shapeStyleDialog.loockStyle(style);

        // define default disable Style
        style = new ShapePainter.Style(ShapePainter.Style.GEOMETRY_QUAD, Color.WHITE, Color.decode("0x750000"), Color.RED, ShapePainter.Style.TEXT_CENTER);
        style.setName("disable");
        shapeStyles.add(style);
        shapeStyleDialog.loockStyle(style);

        // define default enable Style
        style = new ShapePainter.Style(ShapePainter.Style.GEOMETRY_QUAD, Color.WHITE, Color.decode("0x005500"), Color.GREEN, ShapePainter.Style.TEXT_CENTER);
        style.setName("enable");
        style.setBorderSize(1.2);
        shapeStyles.add(style);
        shapeStyleDialog.loockStyle(style);

    }

    private PopupMenu makeGlobalMenu(int enabledControls) {
        makeMenu(GLOBAL_MENU_NAME);

        if ((enabledControls & SELECT_ITEMS) != 0) {
            addMenuItem(GLOBAL_MENU_NAME, "Select All", COMMAND_SELECT_ALL_SHAPES);
        }
        
        if ((enabledControls & ADD_ITEMS) != 0) {
            addMenuItem(GLOBAL_MENU_NAME, "New Item", COMMAND_ADD_SHAPE);
        }
        
        if ((enabledControls & CUSTOMIZE_ITEMS) != 0) {
            addMenuItem(GLOBAL_MENU_NAME, "Edit Shape Styles", COMMAND_CUSTOMIZE_SHAPES_STYLES).setShortcut(new MenuShortcut(KeyEvent.VK_Q, true));
        }

        addMenuItem(GLOBAL_MENU_NAME, "Take Screenshot", COMMAND_TAKE_SCREENSHOT).setShortcut(new MenuShortcut(KeyEvent.VK_T, true));

        // enable click's events listeners
        enableMenu(GLOBAL_MENU_NAME);
        enableEvents();

        return getMenu(GLOBAL_MENU_NAME);
    }

    private PopupMenu makeItemsMenu(int enabledControls) {
        makeMenu(ITEM_MENU_NAME);

        if ((enabledControls & SELECT_ITEMS) != 0) {
            addMenuItem(ITEM_MENU_NAME, "Select All", COMMAND_SELECT_ALL_SHAPES);
        }

        if ((enabledControls & CONFIG_ITEMS) != 0) {
            addMenuItem(ITEM_MENU_NAME, "Rename", COMMAND_RENAME_SHAPE);
        }

        if ((enabledControls & CUSTOMIZE_ITEMS) != 0) {
            addMenuItem(ITEM_MENU_NAME, "Edit Shape Styles", COMMAND_CUSTOMIZE_SHAPES_STYLES);
            addMenuItem(ITEM_MENU_NAME, "Customize", COMMAND_SELECT_SHAPES_STYLE);
        }

        if ((enabledControls & CONFIG_ITEMS) != 0) {
            addSubmenu(ITEM_MENU_NAME, "StateSubMenu", "Edit State");
            addMenuItem("StateSubMenu", "Enable          (Ctrl+E)", COMMAND_ENABLE_SELECTION);
            addMenuItem("StateSubMenu", "Disable         (Ctrl+D)", COMMAND_DISABLE_SELECTION);
        }

        if ((enabledControls & CONECT_ITEMS) != 0) {
            addSubmenu(ITEM_MENU_NAME, "LinkMenu", "Link As");
            addMenuItem("LinkMenu", "SERIE", COMMAND_LINK_SHAPES);
            addMenuItem("LinkMenu", "LOOP", COMMAND_LOOP_LINK_SHAPES);
            addMenuItem("LinkMenu", "STAR", COMMAND_STAR_LINK_SHAPES);
            
            addMenuItem(ITEM_MENU_NAME, "Unlink", COMMAND_UNLINK_SHAPES);
        }

        if ((enabledControls & DELETE_ITEMS) != 0) {
            addMenuItem(ITEM_MENU_NAME, "Delete", COMMAND_DELETE_SHAPES);
        }

        if ((enabledControls & GROUP_ITEMS) != 0) {
            addMenuItem(ITEM_MENU_NAME, "Fix Group", COMMAND_FIX_GROUP);
        }

        addMenuItem(ITEM_MENU_NAME, "Take Screenshot", COMMAND_TAKE_SCREENSHOT);

        // enable click's events listeners
        enableMenu(ITEM_MENU_NAME);
        enableEvents();

        return getMenu(ITEM_MENU_NAME);
    }

    private PopupMenu makeLinksMenu(int enabledControls) {

        makeMenu(LINK_MENU_NAME);

        if ((enabledControls & CUSTOMIZE_ITEMS) != 0) {
            addMenuItem(LINK_MENU_NAME, "Customize", COMMAND_CUSTOMIZE_LINK);
        }

        if ((enabledControls & EDIT_ITEMS) != 0) {
            addMenuItem(LINK_MENU_NAME, "Rename", COMMAND_RENAME_LINK);
        }

        if ((enabledControls & CONFIG_ITEMS) != 0) {
            addMenuItem(LINK_MENU_NAME, "Invert", COMMAND_INVERT_LINK);
        }

        if ((enabledControls & CONECT_ITEMS) != 0) {
            addMenuItem(LINK_MENU_NAME, "Delete", COMMAND_DELETE_LINK);
        }

        addMenuItem(LINK_MENU_NAME, "Take Screenshot", COMMAND_TAKE_SCREENSHOT);

        // enable click's events listeners
        enableMenu(LINK_MENU_NAME);
        enableEvents();

        return getMenu(LINK_MENU_NAME);
    }

    // Item overryde's
    @Override
    public Component addItem(Component item) {
        return null;
    }

    @Override
    public Component removeItem(Component item) {
        return null;
    }

    @Override
    public void removeAll() {
        conectors.clear();
        groups.clear();
        shapes.clear();

        mouseState = NO_SELECTED_GROUP;
        selectedGroup = globalGroup;

        super.removeAll();
    }

    // shape's management
    protected DNode addTextNode(String text) {
        return addTextNode(text, 0.5, 0.5);
    }

    private DNode addTextNode(String text, double x, double y) {
        // create a new node containing text
        DNode node = new DNode(text);

        // define it styles
        node.setSelectedStyle(shapeStyles.get(0));
        node.setDisableStyle(shapeStyles.get(1));
        node.setEnableStyle(shapeStyles.get(2));

        // compute position
        node.setRelativeCenter(x / width, y / height);
        node.setRelativeSpace(0, 0, width, height);
        
        addShape(node);
        return node;
    }

    protected DShape addShape(DShape shape) {

        if (shapes.indexOf(shape) == -1) {
            shape.setParentItem(this);

            // compute location on this relative sub-space origin
            shape.computeRelativeCenter(width, height);
            shape.computeRelativeSize(width, height);
            shape.setRelativeSpace(x, y, width, height);

            // store on graph shape elements list
            shapes.add(shape);
            super.addItem(shape);
            
            update();
        } else {
            System.err.println("Duplicated Element " + shape);
        }

        return shape;
    }

    protected DShape removeShape(DShape shape) {
        if (shapes.contains(shape)) {
            unconectShape(shape);
            super.removeItem(shape);
            shapes.remove(shape);

        }

        return shape;
    }

    protected List<DShape> getShapeByText(String text) {
        List<DShape> matches = new ArrayList<>();

        // select any nodes with match text
        for (DShape shape : shapes) {
            if (shape.getText().equals(text)) {
                matches.add(shape);
            }
        }

        return matches;
    }

    protected DShape selectShape(DShape shape) {
        selectShape(shape, globalGroup.getShapes(), false);
        return shape;
    }

    protected DShape selectShape(DShape shape, boolean isRecursive) {
        disposeShape(shape, globalGroup.getShapes(), isRecursive);
        return shape;
    }

    protected DShape disposeShape(DShape shape) {
        disposeShape(shape, globalGroup.getShapes(), false);
        return shape;
    }

    protected DShape disposeShape(DShape shape, boolean isRecursive) {
        disposeShape(shape, globalGroup.getShapes(), isRecursive);
        return shape;
    }

    private void selectShape(DShape s, ArrayList<DShape> output, boolean isRecursive) {

        if (output.contains(s)) {
            return;
        }

        // select childrens on recursive
        if (isRecursive && s instanceof DShapeGroup) {
            for (DShape si : ((DShapeGroup) s).getShapes()) {
                selectShape(si, output, isRecursive);
            }
        }

        // modify shape state
        s.select(output.size());
        output.add(s);

    }

    private void disposeShape(DShape s, ArrayList<DShape> output, boolean isRecursive) {

        if (!output.contains(s)) {
            return;
        }

        // select childrens on recirsive
        if (isRecursive && s instanceof DShapeGroup) {
            for (DShape si : ((DShapeGroup) s).getShapes()) {
                disposeShape(si, output, isRecursive);
            }
        }

        // modify shape state
        s.dispose();
        output.remove(s);

    }

    // group's management
    private void preserveShapeGroup(DShapeGroup group) {

        if (group != globalGroup) {
            removeShapeGroup(group);
            shapes.add(0, group);

            group.getDisableStyle().setShapeColor(Color.GRAY);
            group.getEnableStyle().setShapeColor(Color.GRAY);
            group.getSelectedStyle().setShapeColor(Color.LIGHT_GRAY);

            // compute location on this relative sub-space origin
            group.computeRelativeCenter(width, height);
            group.setRelativeSpace(x, y, width, height);
            group.dispose();

            super.addItem(group);
        }
    }

    private DShapeGroup addShapeGroup(DShapeGroup group) {

        groups.add(group);
        super.addItem(group);

        group.setSelectedStyle(selectedGroupStyle.duplicate());
        group.setDisableStyle(disableGroupStyle.duplicate());
        group.setEnableStyle(enableGroupStyle.duplicate());
        group.setText("GROUP #" + groups.size());

        return group;
    }

    private DShapeGroup removeShapeGroup(DShapeGroup group) {

        // dispose all graph group items
        group.getShapes().forEach(shape -> {
            shape.dispose();
            shape.setContainer(null);
        });

        updateConectorsState();

        if (group != globalGroup) {
            groups.remove(group);
            super.removeItem(group);

            // reset to global selection
            mouseState = globalGroup.isEmpty() ? NO_SELECTED_GROUP : SELECTED_GROUP;

        } else {
            // clear global group
            group.clear();

            // reset to global selection
            mouseState = NO_SELECTED_GROUP;

        }

        selectedGroup = globalGroup;

        return group;
    }

    // conector's management
    protected DConector conectShapes(DShape shapeA, DShape shapeB, int conectorType) {

        // validate conector policies
        if (shapeA == null || shapeB == null) {
            return null;
        } else if (shapeA instanceof DShapeGroup && !(shapeB instanceof DShapeGroup)) {
            return null;
        } else if (shapeB instanceof DShapeGroup && !(shapeA instanceof DShapeGroup)) {
            return null;
        }

        // search existent conector
        DConector link = getConectorOf(shapeA, shapeB, true);
        if (link != null) {
            return link;
        }

        // search existent inverted conector
        link = getConectorOf(shapeB, shapeA, false);
        if (link != null) {
            link.biconect();
            return link;
        }

        // make a new conector
        link = new DConector(shapeA, shapeB, shapeA.getText() + "-" + shapeB.getText());

        // set conector state styles
        link.setDisableStyle(disableConectorStyle.duplicate()).setPathType(conectorType);
        link.setEnableStyle(enableConectorStyle.duplicate()).setPathType(conectorType);
        link.setSelectedStyle(selectedConectorStyle.duplicate()).setPathType(conectorType);

        // store and add conector
        super.addItem(link);
        conectors.add(link);
        updateConectorsState();

        //System.out.println("Added Link between "+shapeA.getSource()+" AND "+shapeB.getSource());
        return link;
    }

    protected DConector unconectShapes(DShape shapeA, DShape shapeB) {
        DConector conector;

        // validate conector edges
        if (shapeA == null || shapeB == null) {
            return null;
        }

        conector = getConectorOf(shapeA, shapeB, true);

        if (conector != null) {
            conectors.remove(conector);
        }

        return conector;
    }

    protected void unconectShape(DShape shape) {

        // validate conector common edge
        if (shape == null) {
            return;
        }

        Iterator<DConector> iter = conectors.iterator();
        DConector conector;

        while (iter.hasNext()) {
            conector = iter.next();

            if (conector.getShapeA().equals(shape) || conector.getShapeB().equals(shape)) {
                iter.remove();
            }
        }

    }

    private ArrayList<DConector> getConectorsOf(DShape shape) {
        ArrayList<DConector> flinks = new ArrayList<>();

        conectors.forEach((DConector link) -> {
            if (link.getShapeA().equals(shape) || link.getShapeB().equals(shape)) {
                flinks.add(link);
            }
        });

        return flinks;
    }

    private DConector getConectorOf(DShape shapeA, DShape shapeB, boolean acceptBiconceteds) {

        for (DConector conector : conectors) {
            if (conector.getShapeA().equals(shapeA) && conector.getShapeB().equals(shapeB)) {
                return conector;
            } else if (acceptBiconceteds && conector.isBiconected() && conector.getShapeA().equals(shapeB) && conector.getShapeB().equals(shapeA)) {
                return conector;
            }
        }

        return null;
    }

    // miscelaneous
    private void updateConectorsState() {
        conectors.forEach(link -> {
            if (link.getShapeA().isSelected() && link.getShapeB().isSelected()) {
                link.select();
            } else {
                link.dispose();
            }
        });
    }

    private void updateRelativeSpace() {
        // update childrens sub-spaces
        shapes.forEach((shape) -> {
            shape.setRelativeSpace(x, y, width, height);
        });
    }

    private void updateSelectorGroup(DShapeGroup group, Point currentPoint, Point lastPoint) {

        int bx, by, bw, bh;

        if (currentPoint.x <= 0 || currentPoint.x >= width) {
            // limite pointer coords at horizontal bounds
            currentPoint.x = lastPoint.x;
        }

        if (currentPoint.y <= 0 || currentPoint.y >= height) {
            // limite pointer coords at vertical bounds
            currentPoint.y = lastPoint.y;
        }

        // compute selection area geometry bounds
        bx = Math.min(initialPoint.x, currentPoint.x);
        by = Math.min(initialPoint.y, currentPoint.y);
        bw = Math.abs(initialPoint.x - currentPoint.x);
        bh = Math.abs(initialPoint.y - currentPoint.y);

        // update group geometry
        group.setBounds(bx, by, bw, bh);

        // select items inside / deselect items outside
        selectShapesInsideOf(group.getBounds(), true, shapes, group.getShapes());

        // update state
        if (group.isEmpty()) {
            group.dispose();
        } else {
            group.select();
        }

        updateConectorsState();

    }

    protected ArrayList<ShapePainter.Style> getShapeStyles() {
        return this.shapeStyles;
    }

    protected ArrayList<ConectorPainter.Style> getConectorStyles() {
        return this.conectorStyles;
    }

    // items's selection methods
    private void selectAll() {
        shapes.forEach(shape -> selectShape(shape, globalGroup.getShapes(), false));
        mouseState = SELECTED_SHAPES;
    }

    private void disposeAll() {

        // dispose all items on global group
        globalGroup.getShapes().forEach(shape -> {
            shape.dispose();
        });
        globalGroup.clear();

        // dispose all items on groups
        groups.forEach(group -> {
            group.getShapes().forEach(shape -> {
                shape.dispose();
            });
            group.clear();
        });
        groups.clear();

        // dispose all conectors
        conectors.forEach(conector -> conector.dispose());

        // reset to global selection mode
        selectedGroup = globalGroup;
        mouseState = NO_SELECTED_GROUP;

        repaint();
    }

    private DShapeGroup getLastGroupPointedBy(Point point, ArrayList<DShapeGroup> groups) {
        DShapeGroup pointed = null;

        for (DShapeGroup group : groups) {
            if (group.isPointedBy(point)) {
                pointed = group;
            } else {
                group.select();
            }
        }

        return pointed;
    }

    private DShape getLastShapePointedBy(Point point, ArrayList<DShape> input) {
        DShape pointed = null;

        for (DShape shape : input) {
            if (shape.isPointedBy(point)) {
                pointed = shape;
            }
        }

        return pointed;
    }

    private DConector getLastConectorPointedBy(Point point, ArrayList<DConector> input) {
        DConector pointed = null;

        for (DConector conector : input) {
            if (conector.isPointedBy(point)) {
                System.out.println("pointed conector " + conector);
                pointed = conector;
            }
        }

        return pointed;
    }

    private ArrayList<DShape> getShapesInsideOf(Rectangle rectangle, ArrayList<DShape> input) {
        ArrayList<DShape> insided = new ArrayList<>();

        for (DShape shape : input) {
            if (shape.getGeometry().intersects(rectangle)) {
                if (shape instanceof DShapeGroup) {
                    getShapesInsideOfGroup((DShapeGroup) shape, insided);
                } else {
                    insided.add(shape);
                }
            }
        }

        return insided;
    }

    private ArrayList<DShape> getShapesInsideOfGroup(DShapeGroup group, ArrayList<DShape> output) {

        if (output.contains(group) || !group.isEnable()) {
            return output;
        }

        // recurive childs addition
        group.getShapes().forEach(shape -> {
            if (shape instanceof DShapeGroup) {
                getShapesInsideOfGroup((DShapeGroup) shape, output);
            } else {
                output.add(shape);
            }
        });

        output.add(group);

        return output;
    }

    private ArrayList<DShape> selectShapesInsideOf(Rectangle rectangle, boolean canDeselect, ArrayList<DShape> input, ArrayList<DShape> output) {

        ArrayList<DShape> selecteds = getShapesInsideOf(rectangle, input);
        DShape shape;

        // select all insided shapes
        for (int i = 0; i < selecteds.size(); i++) {
            shape = selecteds.get(i);

            if (!shape.isSelected()) {
                selectShape(shape, output, false);
            }

        }

        // deselect no insided shapes
        if (canDeselect) {
            for (int i = 0; i < output.size(); i++) {
                shape = output.get(i);

                if (!selecteds.contains(shape)) {
                    disposeShape(shape, output, false);
                    i--;
                }

            }
        }

        return output;
    }

    // shape selection actions
    private void editShapeText(DShape shape) {
        shape.setText(JOptionPane.showInputDialog("Insert new value:", shape.getText()));
    }

    private void displaceSelection(ArrayList<DShape> selectedShapes, int dx, int dy, boolean lastOnly) {

        if (lastOnly) {
            temp.set(0, pointedShape);
            selectedShapes = temp;
        }

        selectedShapes.forEach(shape -> {

            // dispalce items on recursive
            if (shape instanceof DShapeGroup) {
                displaceSelection(((DShapeGroup) shape).getShapes(), dx, dy, false);
            }

            // update items relative center coords
            shape.displace(dx, dy);
            shape.computeRelativeCenter(width, height);

        });

    }

    private void resizeSelection(ArrayList<DShape> selectedShapes, int dw, int dh, boolean lastOnly) {

        if (lastOnly) {
            temp.set(0, pointedShape);
            selectedShapes = temp;
        }

        selectedShapes.forEach(shape -> {

            int w = shape.getWidth() + dw;
            int h = shape.getHeight() + dh;

            if (w > 20 && w < width && h > 20 && h < height) {
                // change shape size
                shape.setSize(w, h);
                shape.computeRelativeCenter(width, height);

            }

        });
    }

    private void customizeShapeStyles() {
        // show shape styles configuration dialog
        shapeStyleDialog.showDialog();
        update();
    }

    private void selectShapeStyle(ArrayList<DShape> selectedShapes, boolean lastOnly) {

        ShapePainter.Style style;

        // update selection list
        selectionShapeStyleDialog.updateListElements(this::parseName, 1);

        // show selection style dialog
        if (lastOnly) {
            // select by default a current shape style
            selectionShapeStyleDialog.showDialog(shapeStyles.indexOf(pointedShape.isEnable() ? pointedShape.getEnableStyle() : pointedShape.getDisableStyle()));

            temp.set(0, pointedShape);
            selectedShapes = temp;

        } else {
            selectionShapeStyleDialog.showDialog(-1);

        }

        // change style
        if (selectionShapeStyleDialog.hasSelectedItem()) {
            style = selectionShapeStyleDialog.getSelectedItem();

            selectedShapes.forEach((s) -> {
                if (s.isEnable()) {
                    s.setEnableStyle(style);
                } else {
                    s.setDisableStyle(style);
                }
            });

        }
    }

    private void enableShapes(ArrayList<DShape> selectedShapes, boolean lastOnly) {

        if (lastOnly) {
            temp.set(0, pointedShape);
            selectedShapes = temp;
        }

        // enable selected shapes
        selectedShapes.forEach(shape -> {
            shape.enable();
        });

        // update conectors state
        conectors.forEach(conector -> {
            if (conector.getShapeA().isEnable() && conector.getShapeB().isEnable()) {
                conector.enable();
            }
        });

    }

    private void disableShapes(ArrayList<DShape> selectedShapes, boolean lastOnly) {

        if (lastOnly) {
            temp.set(0, pointedShape);
            selectedShapes = temp;
        }

        // disable selected shapes
        selectedShapes.forEach(shape -> {
            shape.disable();
        });

        // update conectors state
        conectors.forEach(conector -> {
            if (conector.isEnable() && !(conector.getShapeA().isEnable() && !conector.getShapeB().isEnable())) {
                conector.enable();
            }
        });

    }

    private void conectShapes(ArrayList<DShape> selectedShapes, boolean centered, boolean fullLoop, int pathType) {
        DShape origin;
        DShape destiny;

        // verify unlinkeable selection
        if (selectedShapes.size() < 2) {
            return;
        }

        if (selectedShapes.size() == 2) {
            conectShapes(selectedShapes.get(0), selectedShapes.get(1), pathType);
            return;
        }

        if (!centered) {
            System.out.println("CONECT SERIAL");
            origin = selectedShapes.get(0);

            // conect all on serial
            for (int i = 1; i < selectedShapes.size(); i++) {
                destiny = selectedShapes.get(i);
                conectShapes(origin, destiny, pathType);

                origin = destiny;
            }
            
            // complete loop
            if (fullLoop) {
                conectShapes(origin, selectedShapes.get(0), pathType);
            }

        } else {

            destiny = selectedShapes.get(0);

            // conect any to first
            for (int i = 1; i < selectedShapes.size(); i++) {
                origin = selectedShapes.get(i);
                conectShapes(origin, destiny, pathType);
                
            }
            
        }

        updateConectorsState();
        update();

    }

    private void unconectShapes(ArrayList<DShape> selectedShapes, boolean lastOnly) {

        DShape last;
        DConector link;

        int size = conectors.size();

        if (!lastOnly) {

            // delete only links of selection pairs
            for (int i = 0; i < size; i++) {
                link = conectors.get(i);

                if (selectedShapes.contains(link.getShapeA()) && selectedShapes.contains(link.getShapeB())) {
                    conectors.remove(i);
                    i--;
                    size--;
                }
            }

        } else {

            last = selectedShapes.get(selectedShapes.size() - 1);

            // delete all links conecteds to last shape
            for (int i = 0; i < size; i++) {
                link = conectors.get(i);

                if (link.getShapeA() == last || link.getShapeB() == last) {
                    conectors.remove(i);
                    i--;
                    size--;
                }
            }

        }

        update();
    }

    private void deleteShapes(ArrayList<DShape> selectedShapes, boolean lastOnly) {

        if (!selectedShapes.isEmpty()) {

            if (lastOnly) {
                temp.set(0, pointedShape);
                selectedShapes = temp;
            }

            selectedShapes.forEach((shape) -> {
                removeShape(shape);
            });

            // disable selection state
            removeShapeGroup(selectedGroup);

        }
    }

    private String parseName(ShapePainter.Style s) {
        return s.getName();
    }

    // geometry state rsponsers
    @Override
    public Shape getGeometry() {
        return getBounds();
    }

    @Override
    public boolean isSizeRelative() {
        return false;
    }

    @Override
    public boolean isExportable() {
        return true;
    }

    // events callbacks
    @Override
    public void onResized(int width, int height) {

        disposeAll();
        updateRelativeSpace();

    }

    @Override
    public void onMoved(int x, int y) {
        updateRelativeSpace();
    }

    @Override
    public void onMouseClicked(MouseEvent e) {

        // clean last pointed items
        pointedGroup = null;
        pointedShape = null;
        pointedConector = null;

        // select last pointed shape
        pointedShape = getLastShapePointedBy(e.getPoint(), shapes);

        // select last pointed group (no fixed)
        pointedGroup = getLastGroupPointedBy(e.getPoint(), groups);

        if (e.getButton() == MouseEvent.BUTTON3) {
            
            // get last mouse coordinates
            lastPoint.setLocation(e.getX(),e.getY());
            
            // select last pointed conector
            pointedConector = getLastConectorPointedBy(e.getPoint(), conectors);

            if (pointedShape != null && pointedGroup == null && pointedConector == null) {

                // select last pointed item or global group
                mouseState = !pointedShape.isSelected ? POINTING_SHAPE : SELECTED_GROUP;
                selectedGroup = globalGroup;

                // show options menu for pointed item
                showMenu(ITEM_MENU_NAME, e.getPoint());

            } else {

                if (pointedConector != null) {
                    mouseState = POINTING_LINK;

                    // show options menu for pointed link
                    showMenu(LINK_MENU_NAME, e.getPoint());
                    selectedGroup = globalGroup;

                } else if (pointedGroup != null) {

                    pointedGroup.select();
                    mouseState = SELECTED_GROUP;

                    // show options menu for group items
                    selectedGroup = pointedGroup;
                    showMenu(ITEM_MENU_NAME, e.getPoint());

                } else {

                    // show global option menu
                    mouseState = NO_SELECTED_GROUP;
                    selectedGroup = globalGroup;

                    showMenu(GLOBAL_MENU_NAME, e.getPoint());
                    System.out.println("Show global");
                }

            }

        } else {

            // select pointed shape
            if ((enabledControlsFlag & SELECT_ITEMS) != 0) {

                // use default selection group
                if (pointedGroup == null) {
                    pointedGroup = globalGroup;
                }

                if (pointedShape != null) {

                    // select or deselect pointed shape
                    if (pointedShape.isSelected()) {
                        disposeShape(pointedShape, pointedGroup.getShapes(), false);
                    } else {
                        selectShape(pointedShape, pointedGroup.getShapes(), false);
                    }

                    // update state of group
                    if (pointedGroup.isEmpty()) {
                        removeShapeGroup(pointedGroup);
                        pointedGroup = globalGroup;
                    } else {
                        pointedGroup.select();
                    }

                } else {

                    if (pointedGroup == globalGroup) {
                        // deselect all groups
                        disposeAll();

                        // define a new mouse state
                        mouseState = NO_SELECTED_GROUP;

                    } else {

                        // update pointed group state
                        if (pointedGroup.isEnable()) {
                            pointedGroup.dispose();
                            pointedGroup = globalGroup;
                        } else {
                            pointedGroup.select();
                        }

                        // define a new mouse state
                        mouseState = SELECTED_GROUP;
                    }

                }

                selectedGroup = pointedGroup;
            }
        }

        // repaint updated UI
        updateConectorsState();
        repaint();

    }

    @Override
    public void onMouseDragged(MouseEvent e, boolean started) {

        int dx, dy;
        boolean canMove, canSelect;

        Point currentPoint = e.getPoint();
        Rectangle bounds = getBounds();

        if (!started) {

            // continue drag operation
            switch (mouseState) {
                case DRAGGING_SHAPE:
                case DRAGGING_GROUP:

                    // compute mouse displacement
                    dx = currentPoint.x - lastPoint.x;
                    dy = currentPoint.y - lastPoint.y;

                    // move shapes groups inner bounds
                    if (bounds.contains(currentPoint)) {
                        selectedGroup.displace(dx, dy);
                        displaceSelection(selectedGroup.getShapes(), dx, dy, mouseState == DRAGGING_SHAPE);
                    }

                    break;

                case MAKING_GROUP:

                    // update items contained inside of selection area
                    updateSelectorGroup(selectedGroup, currentPoint, lastPoint);

                    break;
            }

        } else {

            // select last pointed item
            pointedShape = getLastShapePointedBy(currentPoint, shapes);
            canMove = (enabledControlsFlag & MOVE_ITEMS) != 0;
            canSelect = (enabledControlsFlag & SELECT_ITEMS) != 0;
            
            if (pointedShape != null & canMove) {

                if (!pointedShape.isSelected()) {
                    // drag only a pointed item
                    pointedShape.select();
                    mouseState = DRAGGING_SHAPE;
                    selectedGroup = globalGroup;

                } else {

                    // select last pointed group
                    pointedGroup = getLastGroupPointedBy(currentPoint, groups);

                    if (pointedGroup != null) {
                        selectedGroup = pointedGroup;
                        selectedGroup.select();

                    } else {
                        selectedGroup = globalGroup;

                    }

                    // drag a pointed or global selection group
                    mouseState = DRAGGING_GROUP;

                }

            } else {

                // select last pointed group
                pointedGroup = getLastGroupPointedBy(currentPoint, groups);

                if (pointedGroup != null && canMove) {

                    // drag last pointed group
                    mouseState = DRAGGING_GROUP;
                    selectedGroup = pointedGroup;
                    selectedGroup.select();

                } else {
                    if (canSelect) {

                        // make a new shape group
                        selectedGroup = addShapeGroup(new DShapeGroup());
                        mouseState = MAKING_GROUP;

                        // define initial corner position of group geometry
                        initialPoint.setLocation(currentPoint);
                        updateSelectorGroup(selectedGroup, currentPoint, lastPoint);
                    }
                }

            }

            // update any conectors state
            updateConectorsState();
        }

        // update last pointer position
        lastPoint.setLocation(currentPoint);

        // repaint the updated graph layout
        repaint();

    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        DShape lastShape;

        switch (mouseState) {
            case DRAGGING_SHAPE:
                // dispose pointed item and their links
                pointedShape.dispose();
                mouseState = NO_SELECTED_GROUP;
                selectedGroup = globalGroup;

            case DRAGGING_GROUP:
                // pause selection area drag mode
                selectedGroup.dispose();
                selectedGroup = globalGroup;
                mouseState = NO_SELECTED_GROUP;

                break;

            case MAKING_GROUP:

                if (selectedGroup.isEmpty()) {
                    // remove empty group
                    removeShapeGroup(selectedGroup);
                    selectedGroup = globalGroup;
                    mouseState = NO_SELECTED_GROUP;

                } else {
                    // finish group area making
                    mouseState = SELECTED_GROUP;

                }

                break;
        }

        updateConectorsState();
        repaint();
    }

    @Override
    public void onTypedKey(KeyEvent ke, char key) {

        DShape lastShape = null;

        int value = 0;
        if (selectedGroup.count() > 0) {
            lastShape = selectedGroup.getShapes().get(selectedGroup.count() - 1);
        }

        switch (ke.getKeyCode()) {
            case KeyEvent.VK_A:
                if (ke.isControlDown()) {
                    selectAll();
                } else if (ke.isAltDown()) {
                    disposeAll();
                }
                break;

            case KeyEvent.VK_D:
                if (ke.isControlDown()) {
                    disableShapes(selectedGroup.getShapes(), false);
                }
                break;

            case KeyEvent.VK_E:
                if (ke.isControlDown()) {
                    enableShapes(selectedGroup.getShapes(), false);
                }
                break;

            case KeyEvent.VK_F:
                if (ke.isControlDown()) {
                    selectShapeStyle(selectedGroup.getShapes(), false);
                }
                break;

            case KeyEvent.VK_G:
                if (ke.isAltDown()) {
                    preserveShapeGroup(selectedGroup);
                }
                break;

            case KeyEvent.VK_L:
                if (ke.isControlDown()) {
                    conectShapes(selectedGroup.getShapes(), ke.isAltDown(), true, ConectorPainter.Style.LINE);
                } else if (ke.isShiftDown()) {
                    conectShapes(selectedGroup.getShapes(), false, false, ConectorPainter.Style.LINE);
                } else if (ke.isAltDown()) {
                    conectShapes(selectedGroup.getShapes(), true, false, ConectorPainter.Style.LINE);
                } else {
                    ;
                }

                break;

            case KeyEvent.VK_S:
                if (ke.isAltDown()) {
                    ItemExporter.exporter.export(this);
                }
                break;

            case KeyEvent.VK_R:
                if (ke.isControlDown() && lastShape != null) {
                    editShapeText(lastShape);
                }

                break;

            case KeyEvent.VK_U:
                if (ke.isControlDown()) {
                    unconectShapes(selectedGroup.getShapes(), selectedGroup.count() == 1);
                }

                break;

            case KeyEvent.VK_X:
                if (ke.isControlDown()) {
                    deleteShapes(selectedGroup.getShapes(), false);
                }

                break;

            case KeyEvent.VK_LEFT:
                value = ke.isControlDown() ? -1 : -10;

                if (ke.isShiftDown()) {
                    resizeSelection(selectedGroup.getShapes(), value, 0, false);
                } else {
                    selectedGroup.displace(value, 0);
                    displaceSelection(selectedGroup.getShapes(), value, 0, false);
                }

                break;

            case KeyEvent.VK_RIGHT:
                value = ke.isControlDown() ? 1 : 10;

                if (ke.isShiftDown()) {
                    resizeSelection(selectedGroup.getShapes(), value, 0, false);
                } else {
                    selectedGroup.displace(value, 0);
                    displaceSelection(selectedGroup.getShapes(), value, 0, false);
                }
                break;

            case KeyEvent.VK_UP:
                value = ke.isControlDown() ? -1 : -10;

                if (ke.isShiftDown()) {
                    resizeSelection(selectedGroup.getShapes(), 0, value, false);
                } else {
                    selectedGroup.displace(0, value);
                    displaceSelection(selectedGroup.getShapes(), 0, value, false);
                }
                break;

            case KeyEvent.VK_DOWN:
                value = ke.isControlDown() ? 1 : 10;

                if (ke.isShiftDown()) {
                    resizeSelection(selectedGroup.getShapes(), 0, value, false);
                } else {
                    selectedGroup.displace(0, value);
                    displaceSelection(selectedGroup.getShapes(), 0, value, false);
                }
                break;

            default:
                System.out.println("KEY: " + ke.getKeyCode());
        }
    }

    @Override
    public void onMenuItemSelected(MenuItem item, String command) {

        boolean lastOnly = mouseState == POINTING_SHAPE;

        switch (command) {
            case COMMAND_SELECT_ALL_SHAPES:
                selectAll();
                break;
            
            case COMMAND_ADD_SHAPE:
                addTextNode(shapes.size()+"", lastPoint.getX(), lastPoint.getY());
                break;
                
            case COMMAND_RENAME_SHAPE:
                editShapeText(pointedShape);
                break;

            case COMMAND_CUSTOMIZE_SHAPES_STYLES:
                customizeShapeStyles();
                break;

            case COMMAND_SELECT_SHAPES_STYLE:
                selectShapeStyle(selectedGroup.getShapes(), lastOnly);
                break;

            case COMMAND_ENABLE_SELECTION:
                enableShapes(selectedGroup.getShapes(), lastOnly);
                break;

            case COMMAND_DISABLE_SELECTION:
                disableShapes(selectedGroup.getShapes(), lastOnly);
                break;

            case COMMAND_LINK_SHAPES:
                conectShapes(selectedGroup.getShapes(), false, false, ConectorPainter.Style.LINE);
                break;

            case COMMAND_LOOP_LINK_SHAPES:
                conectShapes(selectedGroup.getShapes(), false, true, ConectorPainter.Style.LINE);
                break;

            case COMMAND_STAR_LINK_SHAPES:
                conectShapes(selectedGroup.getShapes(), true, false, ConectorPainter.Style.LINE);
                break;

            case COMMAND_UNLINK_SHAPES:
                unconectShapes(selectedGroup.getShapes(), lastOnly);
                break;

            case COMMAND_FIX_GROUP:
                preserveShapeGroup(selectedGroup);
                break;

            case COMMAND_DELETE_SHAPES:
                deleteShapes(selectedGroup.getShapes(), lastOnly);
                break;

            case COMMAND_TAKE_SCREENSHOT:
                ItemExporter.exporter.export(this);
                break;

        }

        // return selection to global herarchy
        selectedGroup = globalGroup;
        mouseState = NO_SELECTED_GROUP;

    }

    @Override
    public void onPaint(Graphics2D g) {

        // render groups
        shapes.forEach(shape -> {
            if (shape instanceof DShapeGroup) {
                shape.onPaint(g);
            }
        });

        // render unselected shapes
        shapes.forEach(shape -> {
            if (!shape.isSelected() && !(shape instanceof DShapeGroup)) {
                shape.onPaint(g);
            }
        });

        // render groups
        if (!groups.isEmpty()) {

            // draw selection area geometry
            g.setXORMode(Color.CYAN);
            groups.forEach(group -> group.onPaint(g));
            g.setPaintMode();

        }

        // render selected shapes
        shapes.forEach(shape -> {
            if (shape.isSelected() && !(shape instanceof DShapeGroup)) {
                shape.onPaint(g);
            }
        });

        // render links and un-selected items
        conectors.forEach(link -> link.onPaint(g));

    }

}
