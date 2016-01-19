/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.ClientContextManager;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IClientContext;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.palette.BaseElementConfigurator;
import es.cv.gvcase.mdt.common.palette.BaseElementCreator;
import es.cv.gvcase.mdt.common.palette.Element;
import es.cv.gvcase.mdt.common.palette.ElementConfigurator;
import es.cv.gvcase.mdt.common.palette.ElementCreator;
import es.cv.gvcase.mdt.common.palette.TemplateTool;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * An {@link AbstractCommonTransactionalCommmand} that creates a structure
 * defined in a {@link TemplateTool}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class CreateStructuredTemplateElementsCommand extends
		AbstractCommonTransactionalCommmand {

	// //
	// general variables
	// //

	/**
	 * The {@link TemplateTool} with all the info about the structure and the
	 * elements to create.
	 */
	protected TemplateTool tool = null;

	public TemplateTool getTool() {
		return tool;
	}

	public void setTool(TemplateTool tool) {
		this.tool = tool;
	}

	/**
	 * Root {@link IGraphicalEditPart} with which to create the elements.
	 */
	protected IGraphicalEditPart editPart = null;

	public IGraphicalEditPart getEditPart() {
		return editPart;
	}

	public void setEditPart(IGraphicalEditPart editPart) {
		this.editPart = editPart;
	}

	/**
	 * Root {@link EObject} to create the elements with.
	 */
	protected EObject rootEObject = null;

	public EObject getRootEObject() {
		return rootEObject;
	}

	public void setRootEObject(EObject rootEObject) {
		this.rootEObject = rootEObject;
	}

	/**
	 * The created elements to be returned.
	 */
	protected List<EObject> createdElements = null;

	public List<EObject> getCreatedElements() {
		if (createdElements == null) {
			createdElements = new ArrayList<EObject>();
		}
		return createdElements;
	}

	public void setCreatedElements(List<EObject> createdElements) {
		this.createdElements = createdElements;
	}

	// //
	// constructors
	// //

	/**
	 * Constructor from {@link EditPart}.
	 * 
	 * @param editPart
	 * @param tool
	 */
	public CreateStructuredTemplateElementsCommand(IGraphicalEditPart editPart,
			TemplateTool tool) {
		this(editPart.resolveSemanticElement(), tool);
		this.editPart = editPart;
	}

	/**
	 * Constructor from {@link EObject}.
	 * 
	 * @param rootEObject
	 * @param tool
	 */
	public CreateStructuredTemplateElementsCommand(EObject rootEObject,
			TemplateTool tool) {
		super(TransactionUtil.getEditingDomain(rootEObject),
				"Create elements for " + tool.name, null);
		this.rootEObject = rootEObject;
		this.tool = tool;
	}

	// //
	// 
	// //

	@Override
	public boolean canExecute() {
		return getTool() != null && getEditPart() != null;
	}

	CreateElementRequest createElementRequest = null;

	public CreateElementRequest getCreateElementRequest() {
		return createElementRequest;
	}

	public void setCreateElementRequest(
			CreateElementRequest createElementRequest) {
		this.createElementRequest = createElementRequest;
	}

	// /**
	// * Creates a {@link CreateViewAndElementRequest} from the given
	// parameters.
	// *
	// * @param parent
	// * @param element
	// * @param iHintedType
	// * @return
	// */
	// protected Request getCreatetRequest(EObject parent, Element element,
	// IHintedType iHintedType) {
	// // get the EReference for the element to create in the parent
	// EReference reference = getEReferenceFor(parent.eClass(), element);
	// // create the CreateViewAndElementRequest with all the parafernalia
	// setCreateElementRequest(new CreateElementRequest(parent, iHintedType,
	// reference));
	// CreateElementRequestAdapter createElementRequestAdapter = new
	// CreateElementRequestAdapter(
	// getCreateElementRequest());
	// ViewAndElementDescriptor viewAndElementDescriptor = new
	// ViewAndElementDescriptor(
	// createElementRequestAdapter, Node.class, iHintedType
	// .getSemanticHint(), MDTUtil
	// .getPreferencesHint(getDiagramKind()));
	// CreateViewAndElementRequest createViewAndElementRequest = new
	// CreateViewAndElementRequest(
	// viewAndElementDescriptor);
	// //
	// return createViewAndElementRequest;
	// }
	//
	// protected Command getCreateCommand(EObject parent, Element element,
	// IHintedType iHintedType) {
	// Request createRequest = getCreatetRequest(parent, element, iHintedType);
	// return getEditPart().getCommand(createRequest);
	// }

	// //
	// Command execution
	// //

	/**
	 * 
	 */
	private Map<Object, EObject> mapKey2EObject = null;

	private int COUNTER_INITIAL_VALUE = 0;

	private int counter = COUNTER_INITIAL_VALUE;

	public Map<Object, EObject> getMapKey2EObject() {
		if (mapKey2EObject == null) {
			mapKey2EObject = new HashMap<Object, EObject>();
		}
		return mapKey2EObject;
	}

	protected EPackage getRootEPackage() {
		String uri = getTool().nsURI;
		return EPackage.Registry.INSTANCE.getEPackage(uri);
	}

	protected EFactory getRootEFactory() {
		String uri = getTool().nsURI;
		return EPackage.Registry.INSTANCE.getEFactory(uri);
	}

	protected ElementCreator getElementCreator() {
		if (tool.elementCreator instanceof ElementCreator) {
			return (ElementCreator) tool.elementCreator;
		}
		BaseElementCreator elementCreator = BaseElementCreator.getInstance();
		elementCreator.setBaseEFactory(getRootEFactory());
		elementCreator.setBaseEPackage(getRootEPackage());
		return elementCreator;
	}

	protected ElementConfigurator getElementConfigurator() {
		if (tool.elementConfigurator instanceof ElementConfigurator) {
			return (ElementConfigurator) tool.elementConfigurator;
		}
		return BaseElementConfigurator.getInstance();
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {

		if (doRedo(monitor, info).isOK()) {
			return CommandResult.newOKCommandResult(getCreatedElements());
		}

		return CommandResult.newErrorCommandResult("Error creatin elements");
	}

	@Override
	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// redo operation

		/**
		 * - Creation of all the involved elements that conform the structure.
		 * 1) for each root element that is going to be created: 2) get the
		 * IElementType for the main editpart. 3) get the createcommand from the
		 * edit part with the proper CreationRequest. 4) for each root element
		 * in the structure: 5) create the structure of elements following the
		 * indications of elements created, references in which to create them
		 * and options of creation
		 */

		// prepare initial tools and variables
		getCreatedElements().clear();
		getMapKey2EObject().clear();
		TemplateTool tool = getTool();
		counter = COUNTER_INITIAL_VALUE;

		// try and create each root element.
		List<EObject> createdElements = new ArrayList<EObject>();
		for (Object o : tool.Element) {
			if (o instanceof Element) {
				Element element = (Element) o;
				// createdElements.add(createRootElement(element));
				createdElements.add(createElement(element, getRootEObject()));
			}
		}
		// store new created elements
		setCreatedElements(createdElements);

		// all went well
		return OKStatus;
	}

	/**
	 * Undoing means deleting all the elements that were created.
	 */
	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return OKStatus;
	}

	// protected EObject createRootElement(Element element) {
	// if (element != null) {
	// // created element
	// EObject createdElement = null;
	// // search for the parent, by default, the given parent as parameter
	// EObject parent = searchParent(element, rootEObject);
	// // search for the reference that must contain this new eObject in
	// // its parent
	// EReference reference = getEReferenceFor(parent.eClass(), element);
	// // get the element's IHintedType in the involved diagram
	// IHintedType[] iHintedTypes = getHintedTypesFor(parent, element);
	// if (reference != null) {
	// for (IHintedType type : iHintedTypes) {
	// if (type != null) {
	// Command command = getCreateCommand(parent, element,
	// type);
	// if (command != null) {
	// getEditingDomain().getCommandStack().execute(
	// new GEFtoEMFCommandWrapper(command));
	// if (getCreateElementRequest() != null) {
	// createdElement = getCreateElementRequest()
	// .getNewElement();
	// }
	// }
	// }
	// }
	// if (createdElement != null) {
	// // add the newly created EObject to the mapping
	// storeId2EObject(element, createdElement);
	// // create the structure recursively
	// for (Object o : element.Element) {
	// if (o instanceof Element) {
	// Element childElement = (Element) o;
	// createElement(childElement, createdElement);
	// }
	// }
	// // configure element
	// getElementConfigurator().configureElement(createdElement,
	// element);
	// // return the created EObject
	// return createdElement;
	// }
	// }
	// }
	// return null;
	// }

	/**
	 * 
	 * @param element
	 * @param parent
	 * @return
	 */
	protected EObject createElement(Element element, EObject parent) {
		// get the element creator
		ElementCreator elementCreator = getElementCreator();
		if (element != null) {
			// search for the parent, by default, the given parent as parameter
			parent = searchParent(element, parent);
			// create the element with the creator factory
			EObject eObject = elementCreator.createElement(parent, element);
			// add the newly created EObject to the mapping
			storeId2EObject(element, eObject);
			// add to diagram view
			if (element.addToDiagram != null && element.addToDiagram) {
				addToDiagram(eObject);
			}
			// create the structure recursively
			if (element.Element != null) {
				EObject createdElement = null;
				for (Object o : element.Element) {
					if (o instanceof Element) {
						Element childElement = (Element) o;
						createElement(childElement, eObject);
					}
				}
			}
			// configure element
			getElementConfigurator().configureElement(eObject, element);
			// return the created EObject
			return eObject;
		}
		return null;
	}

	/**
	 * Creates a single instance of the given element
	 * 
	 * @param element
	 * @return
	 */
	protected EObject createElementInstance(Element element) {
		if (element != null) {
			// get the proper factory
			EFactory eFactory = getEFactoryFor(element);
			// create the new EObject
			return eFactory.create(getEclassFor(element));
		}
		return null;
	}

	// //
	//
	// //

	protected void storeId2EObject(Element element, EObject eObject) {
		Object id = getIdForElement(element);
		getMapKey2EObject().put(id, eObject);
	}

	protected EObject searchParent(Element element, EObject parent) {
		if (element.parent != null
				&& getMapKey2EObject().containsKey(element.parent)) {
			return getMapKey2EObject().get(element.parent);
		}
		return parent;
	}

	protected Object getIdForElement(Element element) {
		if (element.createdElementId != null) {
			return element.createdElementId;
		} else {
			counter++;
			return counter;
		}
	}

	// //
	//
	// //

	/**
	 * Adds an element to be shown in the diagram.
	 * 
	 * @param eObject
	 */
	protected void addToDiagram(EObject eObject) {
		if (eObject != null) {
			Diagram diagram = DiagramEditPartsUtil.getDiagramEditPart(
					getEditPart()).getDiagramView();
			if (diagram != null) {
				MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram,
						eObject);
			}
		}
	}

	/**
	 * Gets the {@link IElementType} for the given element in the default
	 * {@link IClientContext}.
	 * 
	 * @param element
	 * @return
	 */
	protected IElementType[] getElementTypesFor(EObject container,
			Element element) {
		if (element != null) {
			EReference reference = getEReferenceFor(container.eClass(), element);
			return ElementTypeRegistry.getInstance().getContainedTypes(
					container, reference);

			// // stub eObject to get the IElementType
			// EObject eObject = createElementInstance(element);
			// if (eObject != null) {
			// // IElementType[] elementTypes =
			// // ElementTypeRegistry.getInstance()
			// // .getAllTypesMatching(eObject, getClientContext());
			// IElementType[] elementTypes = ElementTypeRegistry.getInstance()
			// .getAllTypesMatching(eObject);
			// IClientContext context = getClientContext();
			// ElementTypeRegistry.getInstance().getElementTypes(context);
			//
			// if (elementTypes != null && elementTypes.length > 0) {
			// EClass eClass = getEclassFor(element);
			// if (eClass == null) {
			// return null;
			// }
			// for (IElementType type : elementTypes) {
			// if (eClass.equals(type.getEClass())) {
			// return type;
			// }
			// }
			// }
			// }
		}
		return new IElementType[0];
	}

	/**
	 * Gets the {@link IHintedType} for the given element in the default
	 * {@link IClientContext}.
	 * 
	 * @param element
	 * @return
	 */
	protected IHintedType[] getHintedTypesFor(EObject container, Element element) {
		IElementType[] types = getElementTypesFor(container, element);
		IHintedType[] hintedTypes = null;
		int count = 0;
		for (IElementType type : types) {
			if (type instanceof IHintedType) {
				count++;
			}
		}
		hintedTypes = new IHintedType[count];
		for (int i = 0, j = 0; i < types.length; i++) {
			if (types[i] instanceof IHintedType) {
				hintedTypes[j] = (IHintedType) types[i];
				j++;
			}
		}
		return hintedTypes;
	}

	/**
	 * Get the {@link EFactory} for the given element. Can be different from the
	 * root EFactory if the element specifies a different nsURI.
	 * 
	 * @param element
	 * @return
	 */
	protected EFactory getEFactoryFor(Element element) {
		EFactory eFactory = getRootEFactory();
		if (element.packageUri != null
				&& !element.packageUri
						.equals(eFactory.getEPackage().getNsURI())) {
			eFactory = EPackage.Registry.INSTANCE
					.getEFactory(element.packageUri);
		}
		return eFactory;
	}

	/**
	 * Get the {@link EPackage} for the given element. Can be different from the
	 * root EPackage if the element specifies a different nsURI.
	 */
	protected EPackage getEPackageFor(Element element) {
		EPackage ePackage = getRootEPackage();
		if (element.packageUri != null
				&& !element.packageUri.equals(ePackage.getNsURI())) {
			ePackage = EPackage.Registry.INSTANCE
					.getEPackage(element.packageUri);
		}
		return ePackage;
	}

	protected EClass getEclassFor(Element element) {
		EPackage ePackage = getEPackageFor(element);
		EClassifier classifier = ePackage.getEClassifier(element.name);
		EClass eClass = null;
		if (classifier instanceof EClass) {
			eClass = (EClass) classifier;
		}
		return eClass;
	}

	protected EReference getEReferenceFor(EClass eClass, Element element) {
		if (eClass == null || element == null || element.containment == null) {
			return null;
		}
		EReference reference = null;
		// search the reference by name
		for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
			if (feature instanceof EReference
					&& element.containment.equals(feature.getName())) {
				if (((EReference) feature).isContainment()) {
					reference = (EReference) feature;
				}
			}
		}
		if (reference == null) {
			EClass elementEClass = getEclassFor(element);
			// search the reference by containment feature
			for (EStructuralFeature feature : eClass
					.getEAllStructuralFeatures()) {
				if (feature instanceof EReference
						&& element.containment.equals(feature.getName())) {
					if (((EReference) feature).isContainment()) {
						if (feature.getEType() != null
								&& feature.getEType().equals(elementEClass)) {
							reference = (EReference) feature;
						}
					}
				}
			}
		}
		return reference;
	}

	/**
	 * Get the {@link IClientContext} for the rootEObject.
	 * 
	 * @return
	 */
	protected IClientContext getClientContext() {
		if (getRootEObject() != null) {
			return ClientContextManager.getInstance().getClientContextFor(
					getRootEObject());
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	protected String getDiagramKind() {
		return getEditPart().getNotationView().getDiagram().getType();
	}
}
