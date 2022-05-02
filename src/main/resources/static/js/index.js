$(function() {
	let group = $("#group").val();
	let paths = ["staff", "user"];
	paths.forEach(function(path) {
		let urlObject = new URL(path, location.href);
		urlObject.searchParams.set("group", group);
		let url = urlObject.href

		let link = $("#" + path + "_link");
		link.attr("href", url);
	});

	let qr = $("#qrcode_button");
	const showModal = () => $("#modal").fadeIn();
	qr.on("click", showModal);

	let urlObject = new URL(location.href);
	urlObject.searchParams.set("group", group);
	let url = urlObject.href
	let modal = $("#modal");
	let width = modal.width();
	let height = modal.height();
	let size = Math.floor(Math.min(width, height) * 0.8);
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
