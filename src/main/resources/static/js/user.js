$(function() {
	var group = $("#group").val();
	var seat = $("#seat").val();

	var fn = {};

	fn.ajax = function(url, success) {
		$.ajax({
			url: url,
			type: "POST",
			data: { group: group, seat: seat }
		}).done(data => success(data)).fail(function() {
			const ajax = () => fn.ajax(url, success);
			setTimeout(ajax, 30000);
		});
	}

	fn.check = function() {
		const isEmpty = (str => str == null || str == '');
		if (!isEmpty(group) && !isEmpty(seat)) {
			fn.ajax("/getNumberOfWaiting", function(num) {
				if (num > 0) {
					$("#number_of_waiting").text(num);
					$("#message").show();
					$("#call_button").hide();
					var refreshCycle = $("#ajax_refresh_cycle").text();
					setTimeout(fn.check, refreshCycle * 1000);
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

	var urlObject = new URL("user", location.href);
	urlObject.searchParams.set("group", group);
	var url = urlObject.href;

	new QRCode(document.getElementById("qr_code"), url);
	$("#user_qr").on("click", () => $("#modal").fadeIn());
	$("#modal").on("click", () => $("#modal").fadeOut());
});
