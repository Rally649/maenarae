$(function() {
	var group = $("#group").val();
	var paths = ["staff", "user"];
	paths.forEach(function(path) {
		var urlObject = new URL(path, location.href);
		urlObject.searchParams.set("group", group);
		var url = urlObject.href

		var link = $("#" + path + "_link");
		link.attr("href", url);
	});

	var qr = $("#qrcode_button");
	const showModal = () => $("#modal").fadeIn();
	qr.on("click", showModal);

	var urlObject = new URL(location.href);
	urlObject.searchParams.set("group", group);
	var url = urlObject.href
	var modal = $("#modal");
	var width = modal.width();
	var height = modal.height();
	var size = Math.floor(Math.min(width, height) * 0.8);
	$("#qrcode").html("");
	new QRCode(document.getElementById("qrcode"), {
		text: url,
		width: size,
		height: size
	});

	$("#modal").on("click", () => $("#modal").fadeOut());

	const copyUrl = () => navigator.clipboard.writeText(url).then(alert("コピーしました"));
	$("#url_button").on("click", copyUrl);
})
