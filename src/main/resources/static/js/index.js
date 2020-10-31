$(function() {
	var fn = {};

	fn.updateUrl = function() {
		var group = $("#group").val();
		var paths = ["staff", "user"];
		var message = "グループIDを入力してください";
		paths.forEach(function(path) {
			var urlObject = new URL(path, location.href);
			urlObject.searchParams.set("group", group);
			var url = urlObject.href

			var isValid = (group != "");

			var linkId = "#" + path + "_link";
			$(linkId).text(url);
			$(linkId).off("click");
			$(linkId).on("click", () => isValid ? window.open(url, "_blank") : alert(message));

			var qrId = "#" + path + "_qr";
			$(qrId).off("click");
			$(qrId).on("click", () => isValid ? fn.showModal(linkId) : alert(message));
		});
	}

	fn.showModal = function(id) {
		var url = $(id).text();
		$("#qr_code").html("");
		$("#qr_code").qrcode({ text: url });
		$("#modal").fadeIn();
	}

	fn.updateUrl();
	$("#group").change(fn.updateUrl);

	$("#modal").on("click", () => $("#modal").fadeOut());
})
