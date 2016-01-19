/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Prodevelop) - Initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.CommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GroupEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ListCompartmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.requests.DuplicateRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeUtil;
import org.eclipse.gmf.runtime.emf.clipboard.core.ClipboardSupportUtil;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.emf.type.core.requests.DuplicateElementsRequest;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.DuplicateAnythingCommand;
import es.cv.gvcase.mdt.common.commands.DuplicateViewsCommand;

public class DuplicateEditPolicy extends AbstractEditPolicy {

	@Override
	public boolean understandsRequest(Request req) {
		
		if(RequestConstants.REQ_DUPLICATE.equals(req.getType())) {
			return true;
		}
		
		return super.understandsRequest(req);
	}
	
	@Override
	public Command getCommand(Request request) {
		
		if (RequestConstants.REQ_DUPLICATE.equals(request.getType())) {
            return getDuplicateCommand(((DuplicateRequest) request));
        }
		
		return super.getCommand(request);
	} 
	
	private Command getDuplicateCommand(DuplicateRequest request) {
        List notationViewsToDuplicate = new ArrayList();
        Set elementsToDuplicate = new HashSet();

        for (Iterator iter = request.getEditParts().iterator(); iter.hasNext();) {
            Object ep = iter.next();
            
            // Disable duplicate on groups for now.  See bugzilla 182972.
            if (ep instanceof GroupEditPart) {
                return UnexecutableCommand.INSTANCE;
            }
            
            // modified by gmerin: Duplicate should only be added to elements contained within a ListCompartmentEditPart
            if (ep instanceof IGraphicalEditPart && ((IGraphicalEditPart)ep).getParent() instanceof ListCompartmentEditPart) {
                                
                View notationView = (View)((IGraphicalEditPart) ep).getModel();
                if (notationView != null) {
                    notationViewsToDuplicate.add(notationView);
                }
            }
        }
        
        // Remove views whose container view is getting copied.
        ClipboardSupportUtil.getCopyElements(notationViewsToDuplicate);
        
        TransactionalEditingDomain editingDomain = ((IGraphicalEditPart)getHost()).getEditingDomain();
        
        for (Iterator iter = notationViewsToDuplicate.iterator(); iter
                .hasNext();) {
            View view = (View) iter.next();
            EObject element = view.getElement();

            if (element != null) {
                EObject resolvedElement = EMFCoreUtil.resolve(editingDomain,
                        element);
                if (resolvedElement != null) {
                    elementsToDuplicate.add(resolvedElement);
                }
            }
        }

        /*
         * We must append all inner edges of a node being duplicated. Edges are non-containment
         * references, hence they won't be duplicated for free. Therefore, we add them here to
         * the list views to duplicate.
         * We don't add semantic elements of the edges to the list of semantic elements to duplicate
         * since we assume that their semantic elements are owned by source or target or their semantic
         * containers. 
         */
        HashSet<Edge> allInnerEdges = new HashSet<Edge>();
        for (Iterator itr = notationViewsToDuplicate.iterator(); itr.hasNext();) {
            ViewUtil.getAllRelatedEdgesFromViews(((View)itr.next()).getChildren(), allInnerEdges);
        }
        notationViewsToDuplicate.addAll(allInnerEdges);
        
        if (!notationViewsToDuplicate.isEmpty()) {
            if (!elementsToDuplicate.isEmpty()) {
                DuplicateElementsRequest duplicateElementsRequest = new DuplicateElementsRequest(
                    editingDomain, new ArrayList(elementsToDuplicate));
                Diagram currentDiagram = null;
                if(getHost() instanceof IGraphicalEditPart) {
                	currentDiagram = ((IGraphicalEditPart) getHost()).getNotationView().getDiagram();
                }
				Command duplicateElementsCommand = new ICommandProxy(
						new DuplicateAnythingCommand(editingDomain, duplicateElementsRequest, currentDiagram));
				
                if (duplicateElementsCommand != null
                    && duplicateElementsCommand.canExecute()) {
                    CompositeCommand cc = new CompositeCommand(
                        DiagramUIMessages.Commands_Duplicate_Label);
                    cc
                        .compose(new CommandProxy(
                            duplicateElementsCommand));
                    
                    cc.compose(new DuplicateViewsCommand(editingDomain,
                        DiagramUIMessages.Commands_Duplicate_Label,
                        request, notationViewsToDuplicate,
                        duplicateElementsRequest.getAllDuplicatedElementsMap(), getDuplicateViewsOffset(request)));
                    return new ICommandProxy(cc);
                }
            } else {
                return new ICommandProxy(new DuplicateViewsCommand(editingDomain,
                    DiagramUIMessages.Commands_Duplicate_Label,
                    request, notationViewsToDuplicate, getDuplicateViewsOffset(request)));
            }
        }
        return null;
    }
	
	private Point getDuplicateViewsOffset(DuplicateRequest request) {
        if (request.getOffset() != null) {
            return request.getOffset();
        }
        int offset = MapModeUtil.getMapMode(
            ((org.eclipse.gef.GraphicalEditPart) getHost()).getFigure())
            .DPtoLP(10);
        return new Point(offset, offset);
    }
	
}
