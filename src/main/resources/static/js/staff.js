$(function() {
	var fn = {};
	fn.ajax = function(url, group, seat, success) {
		$.ajax({
			url: url,
			type: "POST",
			data: {
				group: group,
				seat: seat
			}
		}).done(data => success(data));
	}

	var timeout;
	var audio = new Audio("chime.mp3");

	fn.updateCalls = function(isUseingChime) {
		var group = $("#group").val();
		var seat = "";
		fn.ajax("/getCalls", group, seat, function(calls) {
			$("#calls tr").not("#caption").remove();
			calls.forEach(function(call, index) {
				var group = call.group;
				var seat = call.seat;
				var time = call.callTime;

				var buttonId = "call-" + index;

				var row = "<tr>";
				row += "<td>" + seat + "</td>";
				row += "<td>" + time + "</td>";
				row += "<td><button id='" + buttonId + "' class='btn btn-danger'>削除</button></td>";
				row += "</tr>";
				$("#calls").append(row);

				var message = "「" + seat + "」を削除しますか？";
				const updateCalls = () => fn.updateCalls(false);
				const deleteCall = () => fn.ajax("deleteCall", group, seat, updateCalls);
				$("#" + buttonId).on("click", () => confirm(message) && deleteCall());
			});

			clearTimeout(timeout);
			timeout = setTimeout(() => fn.updateCalls(true), 30000);

			if (isUseingChime && calls.length > 0 && $("#chime_check").is(":checked")) {
				audio.play();
			}
		});
	}

	fn.updateCalls(false);

	var group = $("#group").val();
	var url = $("#url").text() + group;
	$("#qr_code").qrcode({ text: url });
	$("#staff_qr").on("click", () => $("#modal").fadeIn());
	$("#modal").on("click", () => $("#modal").fadeOut());
});
