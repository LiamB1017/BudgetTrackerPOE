Group Members:
Eliezer Zlotnick	        ST10312794
Mmabalane Mothiba	        ST10393134
Liam Max Brown	        	ST10262451
Kgomotso Mbulelo Nxumalo	ST10135860
Muhammed Riyaad Kajee	    	ST10395948

The application starts with the Login screen (LoginActivity.kt and activity_login.xml)
There is the company logo at the top with Username/Password field below
User can either log in or register an account

Once logged in the application will open the Monthly Goals page (DashboardActivity.kt, activity_dashboard.xml, MonthlyGoal and MonhtlyGoalDao)
The user MUST enter a value for the minimum and maximum goals and then click the "Update Goals" Button to have it registered
THE BOTTOM of the page has a navigation bar to switch between the 5 screens in the project
This current Monthly Goals page is the first block

Clicking the second block will open the Add Expense page (AddExpense.kt, activity_add_expense.xml, Expense and ExpenseDao)
On this page the user will be asked to allow camera permissions, please allow these for the receipt.
Enter the expense amount, the date, the start and end time, short description and select a category from the dropdown
If there are no Categories there, the user will need to create one in the next page)
The user can optionally attach a receipt by clicking "Attach Receipt", this will open the camera to take a picture.
Once everything is correct the user must click "Upload Expense" to save it

Clicking the third block will open the Category page (CategoryActivity.kt, activity_category.xml, CategoryDatabase, Category and CategoryDao)
The user simply enters the name of their desired category then click "Add Category"
This will update the dropdown list on the previous page (Add Expense page)

Clicking the fourth block will open Expense History page (ExpenseHistoryActivity.kt, activity_expense_history)
The user must select a start and end date by clicking the respective buttons then they must click "Fetch Expense Data)
This will list the expenses in that selectable period.

Clicking the fifth and final block will open Reports page (ReportsActivity.kt, activity_reports.xml)
The user must again pick a start and end date by using the correspondive buttons then click search
Now all the expense names are shown
If the user clicks an expense name from the list, that expenses image receipt will be shown.

