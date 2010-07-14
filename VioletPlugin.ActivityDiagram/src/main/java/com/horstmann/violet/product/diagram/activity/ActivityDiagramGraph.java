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

package com.horstmann.violet.product.diagram.activity;

import java.util.Locale;
import java.util.ResourceBundle;

import com.horstmann.violet.product.diagram.abstracts.AbstractGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.ArrowHead;
import com.horstmann.violet.product.diagram.abstracts.property.BentStyle;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;
import com.horstmann.violet.product.diagram.common.NoteEdge;
import com.horstmann.violet.product.diagram.common.NoteNode;

/**
 * An UML activity diagram.
 */
public class ActivityDiagramGraph extends AbstractGraph
{
    public INode[] getNodePrototypes()
    {
        return NODE_PROTOTYPES;
    }

    public IEdge[] getEdgePrototypes()
    {
        return EDGE_PROTOTYPES;
    }

    private static final INode[] NODE_PROTOTYPES = new INode[9];

    private static final IEdge[] EDGE_PROTOTYPES = new IEdge[2];

    static
    {
        ResourceBundle rs = ResourceBundle.getBundle(ActivityDiagramConstant.ACTIVITY_DIAGRAM_STRINGS, Locale.getDefault());
        
        
        ActivityNode node0 = new ActivityNode();
        node0.setToolTip(rs.getString("node0.tooltip"));
        NODE_PROTOTYPES[0] = node0;
        
        DecisionNode node1 = new DecisionNode();
        node1.setToolTip(rs.getString("node1.tooltip"));
        NODE_PROTOTYPES[1] = node1;
        
        SynchronizationBarNode node2 = new SynchronizationBarNode();
        node2.setToolTip(rs.getString("node2.tooltip"));
        NODE_PROTOTYPES[2] = node2;
        
        SignalSendingNode node3 = new SignalSendingNode();
        node3.setToolTip(rs.getString("node3.tooltip"));
        NODE_PROTOTYPES[3] = node3;
        
        SignalReceiptNode node4 = new SignalReceiptNode();
        node4.setToolTip(rs.getString("node4.tooltip"));
        NODE_PROTOTYPES[4] = node4;
        
        ScenarioStartNode node5 = new ScenarioStartNode();
        node5.setToolTip(rs.getString("node5.tooltip"));
        NODE_PROTOTYPES[5] = node5;
        
        ScenarioEndNode node6 = new ScenarioEndNode();
        node6.setToolTip(rs.getString("node6.tooltip"));
        NODE_PROTOTYPES[6] = node6;
        
        NoteNode node7 = new NoteNode();
        node7.setToolTip(rs.getString("node7.tooltip"));
        NODE_PROTOTYPES[7] = node7;
        
        DiagramLinkNode node8 = new DiagramLinkNode();
        node8.setToolTip(rs.getString("node8.tooltip"));
        NODE_PROTOTYPES[8] = node8;

        ActivityTransitionEdge transition = new ActivityTransitionEdge();
        transition.setBentStyle(BentStyle.VHV);
        transition.setEndArrowHead(ArrowHead.V);
        transition.setToolTip(rs.getString("edge0.tooltip"));
        EDGE_PROTOTYPES[0] = transition;

        NoteEdge noteEdge = new NoteEdge();
        noteEdge.setToolTip(rs.getString("edge1.tooltip"));
        EDGE_PROTOTYPES[1] = noteEdge;
    }

}
