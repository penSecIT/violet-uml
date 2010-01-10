/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.product.diagram.usecase;

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
 * A UML use case diagram.
 */
public class UseCaseDiagramGraph extends AbstractGraph
{

    public INode[] getNodePrototypes()
    {
        return NODE_PROTOTYPES;
    }

    public IEdge[] getEdgePrototypes()
    {
        return EDGE_PROTOTYPES;
    }

    private static final INode[] NODE_PROTOTYPES = new INode[4];

    private static final IEdge[] EDGE_PROTOTYPES = new IEdge[5];

    static
    {
        ResourceBundle rs = ResourceBundle.getBundle(UseCaseDiagramConstant.USECASE_DIAGRAM_STRINGS, Locale.getDefault());

        ActorNode actorNode = new ActorNode();
        actorNode.setToolTip(rs.getString("node0.tooltip"));
        NODE_PROTOTYPES[0] = actorNode;

        UseCaseNode useCaseNode = new UseCaseNode();
        useCaseNode.setToolTip(rs.getString("node1.tooltip"));
        NODE_PROTOTYPES[1] = useCaseNode;

        NoteNode noteNode = new NoteNode();
        noteNode.setToolTip(rs.getString("node2.tooltip"));
        NODE_PROTOTYPES[2] = noteNode;

        DiagramLinkNode diagramLinkNode = new DiagramLinkNode();
        diagramLinkNode.setToolTip(rs.getString("node3.tooltip"));
        NODE_PROTOTYPES[3] = diagramLinkNode;

        UseCaseRelationshipEdge communication = new UseCaseRelationshipEdge();
        communication.setBentStyle(BentStyle.STRAIGHT);
        communication.setLineStyle(LineStyle.SOLID);
        communication.setEndArrowHead(ArrowHead.NONE);
        communication.setToolTip(rs.getString("edge0.tooltip"));
        EDGE_PROTOTYPES[0] = communication;

        UseCaseRelationshipEdge extendRel = new UseCaseRelationshipEdge();
        extendRel.setBentStyle(BentStyle.STRAIGHT);
        extendRel.setLineStyle(LineStyle.DOTTED);
        extendRel.setEndArrowHead(ArrowHead.V);
        extendRel.setMiddleLabel("\u00ABextend\u00BB");
        extendRel.setToolTip(rs.getString("edge1.tooltip"));
        EDGE_PROTOTYPES[1] = extendRel;

        UseCaseRelationshipEdge includeRel = new UseCaseRelationshipEdge();
        includeRel.setBentStyle(BentStyle.STRAIGHT);
        includeRel.setLineStyle(LineStyle.DOTTED);
        includeRel.setEndArrowHead(ArrowHead.V);
        includeRel.setMiddleLabel("\u00ABinclude\u00BB");
        includeRel.setToolTip(rs.getString("edge2.tooltip"));
        EDGE_PROTOTYPES[2] = includeRel;

        UseCaseRelationshipEdge generalization = new UseCaseRelationshipEdge();
        generalization.setBentStyle(BentStyle.STRAIGHT);
        generalization.setLineStyle(LineStyle.SOLID);
        generalization.setEndArrowHead(ArrowHead.TRIANGLE);
        generalization.setToolTip(rs.getString("edge3.tooltip"));
        EDGE_PROTOTYPES[3] = generalization;

        NoteEdge noteEdge = new NoteEdge();
        noteEdge.setToolTip(rs.getString("edge4.tooltip"));
        EDGE_PROTOTYPES[4] = noteEdge;
    }

}
