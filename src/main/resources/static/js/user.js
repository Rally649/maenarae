$(function() {
	var group = $("#group").val();
	var seat = $("#seat").val();

	var fn = {};

	fn.ajax = function(url, success) {
		$.ajax({
			url: url,
			type: "POST",
			data: { group: group, seat: seat }
		}).done(data => success(data));
	}

	fn.check = function() {
		const isEmpty = (str => str == null || str == '');
		if (!isEmpty(group) && !isEmpty(seat)) {
			fn.ajax("/getNumberOfWaiting", function(num) {
				if (num > 0) {
					$("#number_of_waiting").text(num);
					$("#message").show();
					$("#call_button").hide();
					setTimeout(fn.check, 30000);
				} else {
					$("#message").hide();
					$("#call_button").show();
				}
			});
		}
	}

	$("#call_button").on("click", function() {
		fn.ajax("/callStaff", fn.check);
	});

	fn.check();

	var url = $("#url").text() + group;
	$("#qr_code").qrcode({ text: url });
	$("#user_qr").on("click", () => $("#modal").fadeIn());
	$("#modal").on("click", () => $("#modal").fadeOut());
});
