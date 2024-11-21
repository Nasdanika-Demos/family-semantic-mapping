var searchDocuments = {"glossary.html":{"action-uuid":"b96725f5-5c45-427b-b86c-1d716f055caf","title":"Glossary","content":"Clear Identifier(s) Hide UUID {{data.value.name}} {{data.value[0].value}} {{item.value}}"},"references/members/paul/index.html":{"action-uuid":"b6c32b3b-bb28-4032-bb61-205e5559773a","title":"Paul","content":"Paul is a common Latin masculine given name in countries and ethnicities with a Christian heritage (Eastern Orthodoxy, Catholicism, Protestantism) and, beyond Europe, in Christian religious communities throughout the world. Paul - or its variations - can be a given name or surname. Origin and diffusion The name has existed since Roman times. It derives from the Roman family name Paulus or Paullus, from the Latin adjective meaning &ldquo;small&rdquo;, &ldquo;humble&rdquo;, &ldquo;least&rdquo; or &ldquo;little&rdquo; . During the Classical Age it was used to distinguish the minor of two people of the same family bearing the same name. The Roman patrician family of the Gens Aemilia included such prominent persons as Lucius Aemilius Paullus, Lucius Aemilius Paullus Macedonicus, Lucius Aemilius Lepidus Paullus, Tertia Aemilia Paulla (the wife of Scipio Africanus), and Sergius Paulus. Its prevalence in nations with a Christian heritage is primarily due to its attachment to Saint Paul the Apostle, whose Greek name waswas &Pi;&alpha;ῦ&lambda;&omicron;&sigmaf;, Pa&ucirc;los, a transliteration from the Latin, also carrying the &ldquo;modest&rdquo; meaning of this name, and chosen because of its similarity to his Jewish name &Scaron;aul. The name Paul is common, with variations, in all European languages. Paul&rsquo;s popularity has varied. In the United States, the 1990 census shows it ranked the 13th most common (male) name; however, Social Security Administration data shows popularity in the top 20 until 1968, with steadily declining use until its 2015 rank of 200th. The feminine versions are Paula, Pauline, Paulina, and Paulette."},"index.html":{"action-uuid":"e3560360-efa4-444e-81ad-b9ab13e11484","title":"Sample Family","content":"This site was generated as explained below: A Drawio diagram of family relationships was created based on images from the Eclipse Sirius BasicFamily Tutorial. The diagram uses free icons from Icons8 to provide visual distinction between architecture elements. The diagram was mapped the Family model using properties of the diagram elements. Then the family model was transformed to the HTML Application model and a static web site was generated from that model. Icons were generated from the diagram images of the respective elements. The web site is deployed by GitHub Pages. Notes: A click on a diagram element (family member) navigates to the element page. Search provides full-text search which also searches for text in diagrams Search for a family member name finds it in the diagram on the home page Search for ethnicity finds ethnicities on the Paul page There is a link to the source code on GitHub in the footer. Mapping Root -&gt; Family Diagram elements -&gt; Man or Woman Connections -&gt; Father or Mother Generation Representation element filtering Markdown documentation Embedded images PNG resource JPEG resource PNG JPEG Embedded diagrams Draw.io PlantUML Loading from a resource Inline UML Mermaid Loading from a resource Extensions Mapping Root -&gt; Family The root of the diagram is mapped to Family with the type property set to Family This documentation was generated from family.md markdown file with the diagram embedded using drawio fenced block with representations/drawio/diagram expansion token. Diagram semantic elements are mapped to the root semantic element (Family) using the following feature-map: container:\n  self: \n    members:\n      argument-type: Person\n      comparator: label\n The above mapping means that use the semantic element of this diagram element (root) and add semantic elements of its descendants to the members reference ordering by label. argument-type specifies that only instances of Person shall be added to the members reference. This is not really necessary to specify argument-type in this case because the members reference type is Person. page-element set to true specifies that the root semantic element shall also be the page&rsquo;s semantic elements. Because this is the top-level page (not linked from diagram elements), it also becomes the document&rsquo;s semantic element and as such the contents element of the diagram&rsquo;s Ecore resource. Page name &ldquo;Sample Family&rdquo; is used to set the name of the family semantic element. Diagram elements -&gt; Man or Woman Diagram elements are mapped either to Man or Woman. They have semantic-id property to demonstrate its usage. Another way to provide meaningful semantic ID&rsquo;s and URL&rsquo;s is to edit diagram element ID&rsquo;s. Connections -&gt; Father or Mother Connections establish family relationships using feature-map property: source: father specifies that father feature of the connection source semantic element shall be set to the connection target semantic element. source: mother specifies that mother feature of the connection source semantic element shall be set to the connection target semantic element. source: parentsspecifies that the connection target semantic element shall be added to the parents reference of the source semantic element. target: childrenspecifies that the connection source semantic element shall be added to the children reference of the target semantic element. Generation This site was generated with 82 lines of Java code in a JUnit test. Representation element filtering Border of Isa is set during the generation with the following code: @Override\nprotected void filterRepresentationElement(\n\t\tElement representationElement, \n\t\tEObject semanticElement,\n\t\tMap&lt;EObject, EObject&gt; registry,\n\t\tProgressMonitor progressMonitor) {\n\t\t\t\t\n\t// Demo of representation filtering - adding a black border to Isa\n\tif (representationElement instanceof org.nasdanika.drawio.ModelElement) {\n\t\torg.nasdanika.drawio.ModelElement rme = (org.nasdanika.drawio.ModelElement) representationElement;\n\t\tif (&quot;isa&quot;.equals(rme.getProperty(&quot;semantic-id&quot;))) {\n\t\t\trme.getStyle().put(&quot;imageBorder&quot;, &quot;default&quot;);\n\t\t}\n\t}\n}\n Border of Fiona is also set during generation by FamilyRepresentationElementFilter class which is loaded using the Capability framework. This approach allows to decouple representation filtering logic from the generation logic. Representation filtering may be used to inject information which is not available during diagram creation or dynamic, but is available during generation. Markdown documentation This section demonstrates advanced capabilities of Markdown documentation. Embedded images You can embed PNG and JPEG using fenced blocks. PNG resource ```png-resource\nisa.png\n```\n Resource location is resolved relative to the base-uri. JPEG resource ```jpeg-resource\nmy.jpeg\n```\n PNG ```png\nBase 64 encoded png \n```\n JPEG ```jpeg\nBase 64 encoded jpeg\n```\n Embedded diagrams You can also embed PlantUML, Draw.io, and Mermaid diagrams using fenced blocks. Draw.io diagrams can be edited in a desktop editor or Online editor. Draw.io ```drawio-resource\naws.drawio\n```\n Resource location is resolved in the same way as for image files as explained above. PlantUML PlantUML diagrams can be defined inline or loaded from resources. Loading from a resource ```uml-resource\nsequence.plantuml\n```\n Inline The following language specifications (dialects) are supported: uml - for the following diagram types: Sequence, Use Case, Class, Activity, Component, State, Object, Deployment, Timing, Network. wireframe - for Wireframe diagrams gantt - for Gantt diagrams mindmap - for Mind Maps wbs - for Work Breakdown Structures UML Sequence Fenced block: ```uml\nAlice -&gt; Bob: Authentication Request\nBob --&gt; Alice: Authentication Response\n```\n Diagram: Alice -&gt; Bob: Authentication Request Bob --&gt; Alice: Authentication Response Component Component diagram with links to component pages. Fenced block: ```uml\npackage Core {\n   component Common [[https://github.com/Nasdanika/core/tree/master/common]]\n}\n\npackage HTML {\n   component HTML as html [[https://github.com/Nasdanika/html/tree/master/html]]\n   [html] ..&gt; [Common]\n}\n```\n Diagram: package Core { component Common [[https://github.com/Nasdanika/core/tree/master/common]] } package HTML { component HTML as html [[https://github.com/Nasdanika/html/tree/master/html]] [html] ..&gt; [Common] } Wireframe Fenced block: ```wireframe\n{\n  Just plain text\n  [This is my button]\n  ()  Unchecked radio\n  (X) Checked radio\n  []  Unchecked box\n  [X] Checked box\n  &quot;Enter text here   &quot;\n  ^This is a droplist^\n}\n```\n Diagram: { Just plain text [This is my button] () Unchecked radio (X) Checked radio [] Unchecked box [X] Checked box &quot;Enter text here &quot; ^This is a droplist^ } Gantt Fenced block: ```gantt\n[Prototype design] lasts 15 days and links to [[https://docs.nasdanika.org/index.html]]\n[Test prototype] lasts 10 days\n-- All example --\n[Task 1 (1 day)] lasts 1 day\n[T2 (5 days)] lasts 5 days\n[T3 (1 week)] lasts 1 week\n[T4 (1 week and 4 days)] lasts 1 week and 4 days\n[T5 (2 weeks)] lasts 2 weeks\n```\n Diagram: [Prototype design] lasts 15 days and links to [[https://docs.nasdanika.org/index.html]] [Test prototype] lasts 10 days -- All example -- [Task 1 (1 day)] lasts 1 day [T2 (5 days)] lasts 5 days [T3 (1 week)] lasts 1 week [T4 (1 week and 4 days)] lasts 1 week and 4 days [T5 (2 weeks)] lasts 2 weeks Mind Map Fenced block: ```mindmap\n* Debian\n** [[https://ubuntu.com/ Ubuntu]]\n*** Linux Mint\n*** Kubuntu\n*** Lubuntu\n*** KDE Neon\n** LMDE\n** SolydXK\n** SteamOS\n** Raspbian with a very long name\n*** &lt;s&gt;Raspmbc&lt;/s&gt; =&gt; OSMC\n*** &lt;s&gt;Raspyfi&lt;/s&gt; =&gt; Volumio\n```\n Diagram: * Debian ** [[https://ubuntu.com/ Ubuntu]] *** Linux Mint *** Kubuntu *** Lubuntu *** KDE Neon ** LMDE ** SolydXK ** SteamOS ** Raspbian with a very long name *** &lt;s&gt;Raspmbc&lt;/s&gt; =&gt; OSMC *** &lt;s&gt;Raspyfi&lt;/s&gt; =&gt; Volumio WBS WBS elements can have links. This type of diagram can also be used to display organization structure. ```wbs\n* [[https://docs.nasdanika.org/index.html Business Process Modelling WBS]]\n** Launch the project\n*** Complete Stakeholder Research\n*** Initial Implementation Plan\n** Design phase\n*** Model of AsIs Processes Completed\n**** Model of AsIs Processes Completed1\n**** Model of AsIs Processes Completed2\n*** Measure AsIs performance metrics\n*** Identify Quick Wins\n** Complete innovate phase\n```\n Fenced block: Diagram: * [[https://docs.nasdanika.org/index.html Business Process Modelling WBS]] ** Launch the project *** Complete Stakeholder Research *** Initial Implementation Plan ** Design phase *** Model of AsIs Processes Completed **** Model of AsIs Processes Completed1 **** Model of AsIs Processes Completed2 *** Measure AsIs performance metrics *** Identify Quick Wins ** Complete innovate phase Mermaid You can define Mermaid diagrams in mermaid fenced blocks: flowchart LR\n   Alice --&gt; Bob &amp; Chuck --&gt; Deb\n results in this diagram: flowchart LR Alice --&gt; Bob &amp; Chuck --&gt; Deb Loading from a resource It is also possible to load a diagram definition from a resource resolved relative to the model resource: ```mermaid-resource\nsequence.mermaid\n```\n Extensions Table of contents - add [TOC] to the document as explained in the documentation. This extension will create a table of contents from markdown headers. Footnotes Strikethrough: ~~strikethrough~~-&gt; strikethrough Subscript: H~2~O -&gt; H20 Superscript: 2^5^ = 32 -&gt; 25 = 32 Katell Dave Bryan Alan Elias Paul Albert Clara Lea Isa Fiona Katell Dave Bryan Alan Elias Paul Albert Clara Lea Isa Fiona"}}