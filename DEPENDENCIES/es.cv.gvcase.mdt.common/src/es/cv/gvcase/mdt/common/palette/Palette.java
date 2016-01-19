/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.palette;

import java.util.List;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;

/**
 * A customized Palette. Can create a Palette from scratch or customize an
 * already existent one.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class Palette {
	/**
	 * Editor's identifier.
	 */
	public String editorID;
	/**
	 * True -> full new palette will be created. False -> provided palette will
	 * be customized.
	 */
	public Boolean newPalette;
	/**
	 * List of palette containers.
	 */
	public List<Group> Group;
	/**
	 * List of palette tools.
	 */
	public List<Tool> Tool;

	/**
	 * Default empty constructor.
	 */
	public Palette() {
	}

	/**
	 * Customizes a given palette or builds on from scratch.
	 * 
	 * @param paletteRoot
	 * @return
	 */
	public PaletteRoot customizePelette(PaletteRoot paletteRoot) {
		if (newPalette != null && newPalette) {
			// TODO: build palette from scratch
			return paletteRoot;
		} else {
			// modify palette, following these steps:
			// 1) create new groups
			createNewGroups(paletteRoot);
			// 2) create new tools
			createNewTools(paletteRoot);
			// 3) move tools
			moveTools(paletteRoot);
			// 4) remove tools
			removeTools(paletteRoot);
			// 5) remove groups
			removeGroups(paletteRoot);
			// return customized palette
			return paletteRoot;
		}
	}

	/**
	 * Creates not yet existent groups.
	 * 
	 * @param paletteRoot
	 */
	protected void createNewGroups(PaletteRoot paletteRoot) {
		if (this.Group != null) {
			// Create all new groups, and store them in the root.
			for (Group group : this.Group) {
				String groupID = group.groupID;
				PaletteContainer paletteContainer = findPaletteContainer(
						paletteRoot, groupID);
				if (paletteContainer == null) {
					PaletteDrawer paletteGroup = new PaletteDrawer(group.label);
					paletteGroup.setId(groupID);
					setPaletteEntryParent(paletteGroup, paletteRoot);
				}
			}
		}
	}

	/**
	 * Creates not yet existent tools.
	 * 
	 * @param paletteRoot
	 */
	protected void createNewTools(PaletteRoot paletteRoot) {
		if (this.Tool != null) {
			// Create new tools and store them in their proper groups.
			for (Tool tool : this.Tool) {
				String toolID = tool.toolID;
				ToolEntry toolEntry = findToolEntry(paletteRoot, toolID);
				if (toolEntry == null) {
					// TODO: create new tool
				}
			}
		}
	}

	/**
	 * Moves tools to the parent container specified.
	 * 
	 * @param paletteRoot
	 */
	protected void moveTools(PaletteRoot paletteRoot) {
		if (this.Tool != null) {
			// Create new tools and store them in their proper groups.
			for (Tool tool : this.Tool) {
				String toolID = tool.toolID;
				ToolEntry toolEntry = findToolEntry(paletteRoot, toolID);
				if (toolEntry == null) {
					// TODO: weird ...
				} else {
					// tool exists; move it to the specified parent
					String parentID = tool.parentID;
					if (parentID != null) {
						PaletteContainer paletteContainer = findPaletteContainer(
								paletteRoot, parentID);
						if (paletteContainer != null) {
							setPaletteEntryParent(toolEntry, paletteContainer);
							// set tool label and description
							setToolLabel(toolEntry, tool);
							setToolDescription(toolEntry, tool);
						}
					}
				}
			}
		}
	}

	/**
	 * Removes tools specified to be removed.
	 * 
	 * @param paletteRoot
	 */
	protected void removeTools(PaletteRoot paletteRoot) {
		if (this.Tool != null) {
			// Create new tools and store them in their proper groups.
			for (Tool tool : this.Tool) {
				if (tool.remove != null && tool.remove) {
					String toolID = tool.toolID;
					ToolEntry toolEntry = findToolEntry(paletteRoot, toolID);
					if (toolEntry == null) {
						// TODO: weird ...
					} else {
						// tool exists; remove it
						setPaletteEntryParent(toolEntry, null);
					}
				}
			}
		}
	}

	/**
	 * Removes groups specified to be removed.
	 * 
	 * @param paletteRoot
	 */
	protected void removeGroups(PaletteRoot paletteRoot) {
		if (this.Group != null) {
			for (Group group : this.Group) {
				if (group.remove != null && group.remove) {
					String groupID = group.groupID;
					PaletteContainer paletteContainer = findPaletteContainer(
							paletteRoot, groupID);
					if (paletteContainer != null) {
						setPaletteEntryParent(paletteContainer, null);
					}
				}
			}
		}
	}

	/**
	 * Set an entry parent.
	 * 
	 * @param paletteEntry
	 * @param paletteContainer
	 */
	protected void setPaletteEntryParent(PaletteEntry paletteEntry,
			PaletteContainer paletteContainer) {
		if (paletteEntry == null) {
			return;
		}
		if (paletteEntry.getParent() != null) {
			paletteEntry.getParent().remove(paletteEntry);
		}

		if (paletteContainer != null) {
			paletteContainer.add(paletteEntry);
		} else {
			paletteEntry.setParent(null);
		}
	}

	/**
	 * Set an entry label.
	 * 
	 * @param toolEntry
	 * @param tool
	 */
	protected void setToolLabel(ToolEntry toolEntry, Tool tool) {
		if (toolEntry != null && tool != null && tool.label != null
				&& tool.label.length() > 0) {
			toolEntry.setLabel(tool.label);
		}
	}

	/**
	 * Set an entry description.
	 * 
	 * @param entry
	 * @param tool
	 */
	protected void setToolDescription(ToolEntry toolEntry, Tool tool) {
		if (toolEntry != null && tool != null && tool.description != null
				&& tool.description.length() > 0) {
			toolEntry.setDescription(tool.description);
		}
	}

	/**
	 * Find a <PaletteContainer> by id.
	 * 
	 * @param paletteRoot
	 * @param paletteContainerID
	 * @return
	 */
	protected PaletteContainer findPaletteContainer(
			PaletteContainer paletteRoot, String paletteContainerID) {
		PaletteEntry paletteEntry = findPaletteEntry(paletteRoot,
				paletteContainerID);
		return paletteEntry instanceof PaletteContainer ? (PaletteContainer) paletteEntry
				: null;
	}

	/**
	 * Find a <ToolEntry> by id.
	 * 
	 * @param paletteRoot
	 * @param toolEntryID
	 * @return
	 */
	protected ToolEntry findToolEntry(PaletteContainer paletteRoot,
			String toolEntryID) {
		PaletteEntry paletteEntry = findPaletteEntry(paletteRoot, toolEntryID);
		return paletteEntry instanceof ToolEntry ? (ToolEntry) paletteEntry
				: null;
	}

	/**
	 * Find a <PaletteEntry> by id.
	 * 
	 * @param paletteRoot
	 * @param paletteEntryID
	 * @return
	 */
	protected PaletteEntry findPaletteEntry(PaletteContainer paletteRoot,
			String paletteEntryID) {
		if (paletteRoot == null || paletteEntryID == null) {
			return null;
		}
		if (paletteRoot.getChildren() != null) {
			for (Object object : paletteRoot.getChildren()) {
				if (object instanceof PaletteEntry) {
					PaletteEntry paletteEntry = (PaletteEntry) object;
					if (paletteEntryID.equals(paletteEntry.getId())) {
						return paletteEntry;
					}
				}
			}
			for (Object object : paletteRoot.getChildren()) {
				if (object instanceof PaletteContainer) {
					PaletteEntry paletteEntry = findPaletteEntry(
							(PaletteContainer) object, paletteEntryID);
					if (paletteEntry != null) {
						return paletteEntry;
					}
				}
			}
		}
		return null;
	}

}
