window.addEventListener("load", function() {
	let urlObject = new URL("/", location.href);
	let group = document.getElementById("group").value;
	group && urlObject.searchParams.set("group", group);
	document.getElementById("title_link").setAttribute("href", urlObject.href);
});