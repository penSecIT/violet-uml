package com.horstmann.violet.product.diagram.classes;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.horstmann.violet.product.diagram.abstracts.AbstractGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.ArrowHead;
import com.horstmann.violet.product.diagram.abstracts.property.BentStyle;
import com.horstmann.violet.product.diagram.abstracts.property.LineStyle;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;
import com.horstmann.violet.product.diagram.common.NoteEdge;
import com.horstmann.violet.product.diagram.common.NoteNode;

/**
 * A UML class diagram.
 */
public class ClassDiagramGraph extends AbstractGraph
{


    public List<INode> getNodePrototypes()
    {
        return NODE_PROTOTYPES;
    }

    public List<IEdge> getEdgePrototypes()
    {
        return EDGE_PROTOTYPES;
    }

    private static final List<INode> NODE_PROTOTYPES = new ArrayList<INode>();

    private static final List<IEdge> EDGE_PROTOTYPES = new ArrayList<IEdge>();

    static
    {
        ResourceBundle rs = ResourceBundle.getBundle(ClassDiagramConstant.CLASS_DIAGRAM_STRINGS, Locale.getDefault());
        
        ClassNode node0 = new ClassNode();
        node0.setToolTip(rs.getString("node0.tooltip"));
        NODE_PROTOTYPES.add(node0);
        
        InterfaceNode node1 = new InterfaceNode();
        node1.setToolTip(rs.getString("node1.tooltip"));
        NODE_PROTOTYPES.add(node1);
        
        PackageNode node2 = new PackageNode();
        node2.setToolTip(rs.getString("node2.tooltip"));
        NODE_PROTOTYPES.add(node2);
        
        NoteNode node3 = new NoteNode();
        node3.setToolTip(rs.getString("node3.tooltip"));
        NODE_PROTOTYPES.add(node3);
        
        DiagramLinkNode node4 = new DiagramLinkNode();
        node4.setToolTip(rs.getString("node4.tooltip"));
        NODE_PROTOTYPES.add(node4);

        ClassRelationshipEdge dependency = new ClassRelationshipEdge();
        dependency.setLineStyle(LineStyle.DOTTED);
        dependency.setEndArrowHead(ArrowHead.V);
        dependency.setToolTip(rs.getString("edge0.tooltip"));
        EDGE_PROTOTYPES.add(dependency);
        
        ClassRelationshipEdge inheritance = new ClassRelationshipEdge();
        inheritance.setBentStyle(BentStyle.AUTO);
        inheritance.setEndArrowHead(ArrowHead.TRIANGLE);
        inheritance.setToolTip(rs.getString("edge1.tooltip"));
        EDGE_PROTOTYPES.add(inheritance);

        ClassRelationshipEdge interfaceInheritance = new ClassRelationshipEdge();
        interfaceInheritance.setBentStyle(BentStyle.AUTO);
        interfaceInheritance.setLineStyle(LineStyle.DOTTED);
        interfaceInheritance.setEndArrowHead(ArrowHead.TRIANGLE);
        interfaceInheritance.setToolTip(rs.getString("edge2.tooltip"));
        EDGE_PROTOTYPES.add(interfaceInheritance);

        ClassRelationshipEdge association = new ClassRelationshipEdge();
        association.setBentStyle(BentStyle.AUTO);
        association.setEndArrowHead(ArrowHead.V);
        association.setToolTip(rs.getString("edge3.tooltip"));
        EDGE_PROTOTYPES.add(association);

        ClassRelationshipEdge aggregation = new ClassRelationshipEdge();
        aggregation.setBentStyle(BentStyle.AUTO);
        aggregation.setStartArrowHead(ArrowHead.DIAMOND);
        aggregation.setToolTip(rs.getString("edge4.tooltip"));
        EDGE_PROTOTYPES.add(aggregation);

        ClassRelationshipEdge composition = new ClassRelationshipEdge();
        composition.setBentStyle(BentStyle.AUTO);
        composition.setStartArrowHead(ArrowHead.BLACK_DIAMOND);
        composition.setToolTip(rs.getString("edge5.tooltip"));
        EDGE_PROTOTYPES.add(composition);

        NoteEdge noteEdge = new NoteEdge();
        noteEdge.setToolTip(rs.getString("edge6.tooltip"));
        EDGE_PROTOTYPES.add(noteEdge);
    }

}
