$(function() {
	var urlObject = new URL("/", location.href);
	var group = $("#group").val();
	group && urlObject.searchParams.set("group", group);
	$("#title_link").attr("href", urlObject.href);
});