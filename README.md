# Glossary Editor

This repository contains the set of Eclipse plug-ins that implement the glossary editor that is integrated in the MOSKitt platform (https://www.prodevelop.es/en/products/MOSKitt). This editor allows the user to define (as models) glossaries of terms by means of a form-based graphical user interface.

To develop the glossary editor, I used the FEFEM framework (see "/DEPENDENCIES/es.cv.gvcase.fefem.common"), which I also developed alongside my colleagues of the MOSKitt team. FEFEM is based on model-driven Eclipse technologies such as the Eclipse Modeling Framework (EMF) and JFace Databinding. An introduction to the development of form-based editors using FEFEM is provided in the following presentation:

http://www.slideshare.net/mcervera/development-of-forms-editors-based-on-ecore-metamodels-presentation-793692

The glossary editor was developed using version 3.5 of the Eclipse Modeling Tools, which can be downloaded from:

http://www.eclipse.org/downloads/packages/release/Galileo/SR2

Steps for installation and testing:

1. Import the plug-ins of this repository (both in the root folder and the DEPENDENCIES folder) into the Eclipse workspace.
2. Extract the content of the file "dropins.zip" into the "dropins" folder of Eclipse.
3. Run a second instance of Eclipse (Run as -> Eclipse Application).
4. In this second instance, create a file of type "Glossary".
