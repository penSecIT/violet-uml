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

package com.horstmann.violet.product.diagram.sequence;

import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.ResourceBundle;

import com.horstmann.violet.product.diagram.abstracts.AbstractGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;
import com.horstmann.violet.product.diagram.common.NoteEdge;
import com.horstmann.violet.product.diagram.common.NoteNode;

/**
 * A UML sequence diagram.
 */
public class SequenceDiagramGraph extends AbstractGraph
{
//    public boolean addNode(INode n, Point2D p)
//    {
//        if (!super.addNode(n, p)) return false;
//        if (n instanceof ActivationBarNode) // must be inside an object
//        {
//            Collection<INode> nodes = getNodes();
//            boolean inside = false;
//            Iterator<INode> iter = nodes.iterator();
//            while (!inside && iter.hasNext())
//            {
//                INode n2 = iter.next();
//                if (n2 instanceof LifelineNode && n2.contains(p))
//                {
//                    inside = true;
//                    ((ActivationBarNode) n).setImplicitParameter((LifelineNode) n2);
//                }
//            }
//            if (!inside) return false;
//        }
//        return true;
//    }
    
    @Override
    public boolean addNode(INode newNode, Point2D p)
    {
        INode foundNode = findNode(p);
        if (foundNode == null && newNode.getClass().isAssignableFrom(ActivationBarNode.class)) {
            return false;
        }
        return super.addNode(newNode, p);
    }

    /*
     * public void addEdgeToRemove(Edge e) { if (e instanceof CallEdge && e.getEnd().getChildren().size() == 0)
     * addNodeToRemove(e.getEnd()); super.addEdgeToRemove(e); }
     */

 

    public INode[] getNodePrototypes()
    {
        return NODE_PROTOTYPES;
    }

    public IEdge[] getEdgePrototypes()
    {
        return EDGE_PROTOTYPES;
    }

    private static final INode[] NODE_PROTOTYPES = new INode[4];

    private static final IEdge[] EDGE_PROTOTYPES = new IEdge[3];

    static
    {
        ResourceBundle rs = ResourceBundle.getBundle(SequenceDiagramConstant.SEQUENCE_DIAGRAM_STRINGS, Locale.getDefault());
        
        LifelineNode lifelineNode = new LifelineNode();
        lifelineNode.setToolTip(rs.getString("node0.tooltip"));
        NODE_PROTOTYPES[0] = lifelineNode;
        
        ActivationBarNode activationBarNode = new ActivationBarNode();
        activationBarNode.setToolTip(rs.getString("node1.tooltip"));
        NODE_PROTOTYPES[1] = activationBarNode;
        
        NoteNode noteNode = new NoteNode();
        noteNode.setToolTip(rs.getString("node2.tooltip"));
        NODE_PROTOTYPES[2] = noteNode;
        
        DiagramLinkNode diagramLinkNode = new DiagramLinkNode();
        diagramLinkNode.setToolTip(rs.getString("node3.tooltip"));
        NODE_PROTOTYPES[3] = diagramLinkNode;
        
        CallEdge callEdge = new CallEdge();
        callEdge.setToolTip(rs.getString("edge0.tooltip"));
        EDGE_PROTOTYPES[0] = callEdge;
        
        ReturnEdge returnEdge = new ReturnEdge();
        returnEdge.setToolTip(rs.getString("edge1.tooltip"));
        EDGE_PROTOTYPES[1] = returnEdge;
        
        NoteEdge noteEdge = new NoteEdge();
        noteEdge.setToolTip(rs.getString("edge2.tooltip"));
        EDGE_PROTOTYPES[2] = noteEdge;
    }

}
