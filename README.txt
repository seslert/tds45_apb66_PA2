Adam Boe	apb66@case.edu
Tim Sesler	tds45@case.edu
Programming Project 2
6 March 2015

The project works well with 1 or 2 plies, but we understand that it is incredibly inefficient and will 
take awhile to complete plies of 4 or higher.  Our heuristic takes into account the utility of each state 
so we had to create utilities when each state is created.  Furthermore, these utilities are based off of 
resource intensive distance calculations which certainly does not help runtime.  We should have made the factors 
that the heuristic considers more confined to the heuristic method (e.g., we give a utility of infinity if 
a footman can attack when the attack state is created, not when the heuristic looks at the state).  We should 
have only calculated the utility at leaf nodes instead of every node...

This project will not work with the obstacles unless you want to watch the footmen suffer and die.  We really 
struggled with time management on this project and it is honestly a miracle that we managed to get it working 
with two footmen in time.  We understand any penalties imposed for this lack of functionality.