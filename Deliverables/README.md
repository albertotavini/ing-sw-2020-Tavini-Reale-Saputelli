# SANTORINI

Welcome to Santorini, a Cranio Creations game adapted by Tavini, Reale and Saputelli (GC27)



What we chose to implement was:

Regole Complete + CLI + GUI + Socket + 2FA

In particular for advanced functionalities we opted for:

- Multiple matches

- Advanced Gods (Hestia, Ares, Triton, Zeus, Chronus)



**Additional features**

- Serialization and Deserialization of Gods from Json File, with the possibility to insert personalized decks of gods

- A chat that players can use while playing with the GUI 

- In addition to the type of lobbies required from specification (that we called Casual) we gave the possibility to create and join public and private lobbies with name and eventually a password. Casual lobbies can be chosen via "join public lobby" option.



**TESTING**

By considering only the classes for which testing was required, from the report on coverage of tests we obtain 97,4% for method coverage and 91,9% for line coverage. Report is included in the Deliverables folder.

**UML's**

The folder includes a .rar with different UMLs that focus on single packages or parts of the program, then there is a complete one  with the entire project, and a complete one with only names of the classes and relations between them. 
There is also an updated version (circa 7th of april) of the first draft of the UML we did when we first planned the project. 

**Javadoc**

There are also two folders for Javadoc because for model and controller (and in some other parts of the code) we commented also private classes and methods, and both versions can be consulted depending on how much one wants to go in depth in the the code.



**INSTRUCTIONS**

In the folder with the Jar files you will find the .bat to allow the game to run on local host. In particular the Server has an optional argument which is a path for saving a logfile (if not used, the server simply won't print any log) and the Client will take "c" or "g" as first argument to decide wheter you want to play with CLI or GUI, while the second argument is the IP address to which it will connect (in the .bat we used 127.0.0.1).