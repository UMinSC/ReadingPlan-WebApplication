/**
 * 
 */
function verify() {
	var username = document.forms['form']['username'].value;
	var password1 = document.forms['form']['password'].value;
	var password2 = document.forms['form']['verifyPassword'].value;

	if (username.length < 5 || password1 == null || password1 == "" || password1 != password2) {
		document.getElementById("error").innerHTML = "Please check your username_length(<5) or passwords";
		return false;
	}
}

function checkBookData() {
	var title = document.forms['bookForm']['title'].value.trim();
	var author = document.forms['bookForm']['author'].value.trim();
	var isbn = document.forms['bookForm']['isbn'].value.trim();
	var price = document.forms['bookForm']['price'].value.trim();


	if (title === "" || author === "" || isbn === "" || price === "") {
		document.getElementById("error").innerHTML = "Please fill in all fields";
		document.getElementById("success").innerHTML = "";
		return false;
	}
}

var statusIds = /*[[${statusIds}]]*/ [];
var lastTabIndex = -1;
function showBookList(tabIndex) {
    
    if (lastTabIndex !== -1) {
        const lastPanel = document.getElementById(`panel-${lastTabIndex}`);
        if (lastPanel) {
            lastPanel.hidden = true;
        }
    }

    const panelToShow = document.getElementById(`panel-${tabIndex}`);
    if (panelToShow) {
        panelToShow.hidden = false;
    }

   
    lastTabIndex = tabIndex;
}


