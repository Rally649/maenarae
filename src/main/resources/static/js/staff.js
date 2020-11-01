$(function() {
	var fn = {};
	fn.ajax = function(url, group, seat, success) {
		$.ajax({
			url: url,
			type: "POST",
			data: { group: group, seat: seat }
		}).done(data => success(data));
	}

	var timeout;
	var audio = new Audio("chime.mp3");

	fn.updateCalls = function(isUsingChime) {
		var group = $("#group").val();
		var seat = "";
		fn.ajax("/getCalls", group, seat, function(calls) {

			const format = function(callTime) {
				const slice = num => ("0" + num).slice(-2);

				var date = new Date(callTime);
				var YYYY = date.getFullYear();
				var MM = slice(date.getMonth() + 1);
				var DD = slice(date.getDate());
				var hh = slice(date.getHours());
				var mm = slice(date.getMinutes());
				var ss = slice(date.getSeconds());
				return YYYY + "-" + MM + "-" + DD + " " + hh + ":" + mm + ":" + ss;
			}

			$("#calls tr").not("#caption").remove();
			calls.forEach(function(call) {
				var group = call.groupId;
				var seat = call.seatId;
				var time = format(call.callTime);

				var tr = $("<tr>").appendTo($("#calls"));
				$("<td>").text(seat).appendTo(tr);
				$("<td>").text(time).appendTo(tr);

				var message = "「" + seat + "」を削除しますか？";
				const updateCalls = () => fn.updateCalls(false);
				const deleteCall = () => fn.ajax("deleteCall", group, seat, updateCalls);
				const buttonAction = () => confirm(message) && deleteCall();
				var button = $("<button>").text("削除").addClass("btn btn-danger").on("click", buttonAction);
				button.appendTo($("<td>").appendTo(tr));
			});

			clearTimeout(timeout);
			timeout = setTimeout(() => fn.updateCalls(true), 30000);

			if (isUsingChime && calls.length > 0 && $("#chime_check").is(":checked")) {
				audio.play();
			}
		});
	}

	fn.updateCalls(false);

	var group = $("#group").val();

	var urlObject = new URL("staff", location.href);
	urlObject.searchParams.set("group", group);
	var url = urlObject.href;
	$("#qr_code").qrcode({ text: url });
	$("#staff_qr").on("click", () => $("#modal").fadeIn());
	$("#modal").on("click", () => $("#modal").fadeOut());
});
