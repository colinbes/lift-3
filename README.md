# Liftweb 3.0, dynamic menu example

- build system: MVN
- IDE: Eclipse (Scala IDE) 
- less file *mystyles.less* is used to manage and build the mystyles.css file 
    css file is not version controlled so will need to be generated.
    ```
    cd <project>/src/main/webapp/assets/css
    lessc ../less/mystyles.less mystyles.css
    ```
Stylesheet could be automated with MVN if required.

Note, this template is mix of various different examples found on the web with my own edits and spin on a Liftweb template that I typically use.

To login, use any valid email format for email address and password of *password*, **Note** email is not used anywhere, it's simply a placeholder.

Once logged in there will be an additional **Eula** menu item under **User** on top right. Inspecting Eula html code shows issue discussed of nested anchors. 

Inspection should show html code:
```
<li><a href="/userEula"></a><a href="api/eula/abc@abc.non" target="_blank">Eula</a></li>
```
 in safari/chrome and 
```
<li><a href="..."><a href="api/eula/id">Eula</a></a></li>
```
causing error.