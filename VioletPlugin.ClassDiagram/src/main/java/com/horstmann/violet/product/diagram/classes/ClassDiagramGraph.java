package com.horstmann.violet.product.diagram.classes;


import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.ResourceBundle;

import com.horstmann.violet.framework.diagram.AbstractGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.property.ArrowHead;
import com.horstmann.violet.framework.diagram.property.BentStyle;
import com.horstmann.violet.framework.diagram.property.LineStyle;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;
import com.horstmann.violet.product.diagram.common.NoteEdge;
import com.horstmann.violet.product.diagram.common.NoteNode;

/**
 * A UML class diagram.
 */
public class ClassDiagramGraph extends AbstractGraph
{

    public boolean addEdgeAtPoints(IEdge e, Point2D p1, Point2D p2)
    {
        // Node n1 = findNode(p1);
        // Node n2 = findNode(p2);
        // if (n1 == n2) return false;
        return super.addEdgeAtPoints(e, p1, p2);
    }

    public INode[] getNodePrototypes()
    {
        return NODE_PROTOTYPES;
    }

    public IEdge[] getEdgePrototypes()
    {
        return EDGE_PROTOTYPES;
    }

    private static final INode[] NODE_PROTOTYPES = new INode[5];

    private static final IEdge[] EDGE_PROTOTYPES = new IEdge[7];

    static
    {
        ResourceBundle rs = ResourceBundle.getBundle(ClassDiagramConstant.CLASS_DIAGRAM_STRINGS, Locale.getDefault());
        
        ClassNode node0 = new ClassNode();
        node0.setToolTip(rs.getString("node0.tooltip"));
        NODE_PROTOTYPES[0] = node0;
        
        InterfaceNode node1 = new InterfaceNode();
        node1.setToolTip(rs.getString("node1.tooltip"));
        NODE_PROTOTYPES[1] = node1;
        
        PackageNode node2 = new PackageNode();
        node2.setToolTip(rs.getString("node2.tooltip"));
        NODE_PROTOTYPES[2] = node2;
        
        NoteNode node3 = new NoteNode();
        node3.setToolTip(rs.getString("node3.tooltip"));
        NODE_PROTOTYPES[3] = node3;
        
        DiagramLinkNode node4 = new DiagramLinkNode();
        node4.setToolTip(rs.getString("node4.tooltip"));
        NODE_PROTOTYPES[4] = node4;

        ClassRelationshipEdge dependency = new ClassRelationshipEdge();
        dependency.setLineStyle(LineStyle.DOTTED);
        dependency.setEndArrowHead(ArrowHead.V);
        dependency.setToolTip(rs.getString("edge0.tooltip"));
        EDGE_PROTOTYPES[0] = dependency;
        
        ClassRelationshipEdge inheritance = new ClassRelationshipEdge();
        inheritance.setBentStyle(BentStyle.VHV);
        inheritance.setEndArrowHead(ArrowHead.TRIANGLE);
        inheritance.setToolTip(rs.getString("edge1.tooltip"));
        EDGE_PROTOTYPES[1] = inheritance;

        ClassRelationshipEdge interfaceInheritance = new ClassRelationshipEdge();
        interfaceInheritance.setBentStyle(BentStyle.VHV);
        interfaceInheritance.setLineStyle(LineStyle.DOTTED);
        interfaceInheritance.setEndArrowHead(ArrowHead.TRIANGLE);
        interfaceInheritance.setToolTip(rs.getString("edge2.tooltip"));
        EDGE_PROTOTYPES[2] = interfaceInheritance;

        ClassRelationshipEdge association = new ClassRelationshipEdge();
        association.setBentStyle(BentStyle.HVH);
        association.setEndArrowHead(ArrowHead.V);
        association.setToolTip(rs.getString("edge3.tooltip"));
        EDGE_PROTOTYPES[3] = association;

        ClassRelationshipEdge aggregation = new ClassRelationshipEdge();
        aggregation.setBentStyle(BentStyle.HVH);
        aggregation.setStartArrowHead(ArrowHead.DIAMOND);
        aggregation.setToolTip(rs.getString("edge4.tooltip"));
        EDGE_PROTOTYPES[4] = aggregation;

        ClassRelationshipEdge composition = new ClassRelationshipEdge();
        composition.setBentStyle(BentStyle.HVH);
        composition.setStartArrowHead(ArrowHead.BLACK_DIAMOND);
        composition.setToolTip(rs.getString("edge5.tooltip"));
        EDGE_PROTOTYPES[5] = composition;

        NoteEdge noteEdge = new NoteEdge();
        noteEdge.setToolTip(rs.getString("edge6.tooltip"));
        EDGE_PROTOTYPES[6] = noteEdge;
    }

}
