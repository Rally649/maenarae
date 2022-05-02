$(function() {
	let urlObject = new URL("/", location.href);
	let group = $("#group").val();
	group && urlObject.searchParams.set("group", group);
	$("#title_link").attr("href", urlObject.href);
});