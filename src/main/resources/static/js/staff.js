$(function() {
	var group = $("#group").val();

	var fn = {};
	fn.ajax = function(url, seat, success) {
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
		fn.ajax("/getCalls", "", function(calls) {
			$("#calls tr").not("#caption").remove();
			calls.forEach(function(call) {
				var seat = call.seat;
				var time = call.callTime;

				var row = "<tr>";
				row += "<td>" + seat + "</td>";
				row += "<td>" + time + "</td>";
				row += "<td><button id='" + seat + "' class='btn btn-danger'>削除</button></td>";
				row += "</tr>";
				$("#calls").append(row);

				var message = "「" + seat + "」を削除しますか？";
				const updateCalls = () => fn.updateCalls(false);
				const deleteCall = () => fn.ajax("deleteCall", seat, updateCalls);
				$("#" + seat).on("click", () => confirm(message) && deleteCall());
			});

			clearTimeout(timeout);
			timeout = setTimeout(() => fn.updateCalls(true), 30000);

			if (isUseingChime && calls.length > 0 && $("#chime_check").is(":checked")) {
				audio.play();
			}
		});
	}

	fn.updateCalls(false);

	var url = $("#url").text() + group;
	$("#qr_code").qrcode({ text: url });
	$("#staff_qr").on("click", () => $("#modal").fadeIn());
	$("#modal").on("click", () => $("#modal").fadeOut());
});
