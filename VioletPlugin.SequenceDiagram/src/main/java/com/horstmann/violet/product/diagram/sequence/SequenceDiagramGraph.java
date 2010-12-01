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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.horstmann.violet.product.diagram.abstracts.AbstractGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;
import com.horstmann.violet.product.diagram.common.NoteEdge;
import com.horstmann.violet.product.diagram.common.NoteNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

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

    public void layout(Graphics2D g2, IGrid grid)
    {
        List<ActivationBarNode> topLevelCalls = new ArrayList<ActivationBarNode>();
        List<LifelineNode> objects = new ArrayList<LifelineNode>();
        List<INode> otherNodes = new ArrayList<INode>();
        for (INode n : getNodes())
        {
            if (n instanceof ActivationBarNode && n.getParent() == null) topLevelCalls.add((ActivationBarNode) n);
            else if (n instanceof LifelineNode) objects.add((LifelineNode) n);
            else if (n.getParent() == null) otherNodes.add(n);
        }

//        for (IEdge e : getEdges())
//        {
//            if (e instanceof CallEdge)
//            {
//                INode end = e.getEnd();
//                if (end instanceof ActivationBarNode) ((ActivationBarNode) end).setSignaled(((CallEdge) e).isSignal());
//            }
//        }

        // find the max of the heights of the objects

        double top = 0;
        for (LifelineNode n : objects)
        {
            if (n.getParent() == null)
            {
                n.setZ(0);
                n.translate(0, -n.getLocation().getY());
                n.layout(g2, grid);
                top = Math.max(top, n.getTopRectangle().getMaxY());
            }
        }

        Collections.sort(topLevelCalls, new Comparator<INode>()
        {
            public int compare(INode n1, INode n2)
            {
                double d = n1.getLocation().getY() - n2.getLocation().getY();
                if (d < 0) return -1;
                if (d > 0) return 1;
                d = n1.getLocation().getX() - n2.getLocation().getX();
                if (d < 0) return -1;
                if (d > 0) return 1;
                return 0;
            }
        });

        for (ActivationBarNode call : topLevelCalls)
        {
            top += ActivationBarNode.CALL_YGAP;
            call.translate(0, top - call.getLocation().getY());
            call.setZ(1);
            call.layout(g2, grid);
            top += call.getBounds().getHeight();
        }

        top += ActivationBarNode.CALL_YGAP;
        // set uniform heights of life lines
        for (LifelineNode n : objects)
        {
            Rectangle2D b = n.getBounds();
            n.setBounds(new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth(), top - b.getY()));
        }

        for (INode n : otherNodes)
        {
            n.layout(g2, grid);
        }
    }

    /*
     * public void draw(Graphics2D g2, Grid g) { layout(g2, g);
     * 
     * Collection<Node> nodes = getNodes(); Iterator<Node> iter = nodes.iterator(); while (iter.hasNext()) { Node n = (Node)
     * iter.next(); if (!(n instanceof ActivationBarNode)) n.draw(g2); }
     * 
     * iter = nodes.iterator(); while (iter.hasNext()) { Node n = iter.next(); if (n instanceof ActivationBarNode) n.draw(g2); }
     * 
     * Collection<Edge> edges = getEdges(); Iterator<Edge> iter2 = edges.iterator(); while (iter2.hasNext()) { Edge e =
     * iter2.next(); e.draw(g2); } }
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
