$(function() {
	var fn = {};

	fn.updateUrl = function() {
		var group = $("#group").val();
		var paths = ["staff", "user"];
		const showAlert = () => alert("グループIDを入力してください");
		paths.forEach(function(path) {
			var urlObject = new URL(path, location.href);
			urlObject.searchParams.set("group", group);
			var url = urlObject.href

			var isValid = (group != "");

			var link = $("#" + path + "_link");
			link.text(url);
			link.attr("href", isValid ? url : "#");
			link.off("click").on("click", isValid ? null : showAlert);

			var qr = $("#" + path + "_qr");
			const showModal = () => fn.showModal(link);
			qr.off("click").on("click", isValid ? showModal : showAlert);
		});
	}

	fn.showModal = function(link) {
		var url = link.text();
		$("#qr_code").html("").qrcode({ text: url });
		$("#modal").fadeIn();
	}

	fn.updateUrl();
	$("#group").change(fn.updateUrl);

	$("#modal").on("click", () => $("#modal").fadeOut());
})
