$(function() {
	var fn = {};

	var url = new URL(location.href);
	var params = url.searchParams;
	var group = params.get("group");
	var seat = params.get("seat");

	const isEmpty = (str => str == null || str == '');

	fn.ajax = function(url, group, seat, success) {
		if (!isEmpty(group) && !isEmpty(seat)) {
			$.ajax({
				url: url,
				type: "POST",
				data: { group: group, seat: seat }
			}).done(data => success(data)).fail(function() {
				const ajax = () => fn.ajax(url, success);
				setTimeout(ajax, 30000);
			});
		}
	}

	fn.check = function() {
		fn.ajax("/getNumberOfWaiting", group, seat, function(num) {
			if (num > 0) {
				$("#number_of_waiting").text(num);
				$("#message").show();
				$("#ok_button").hide();
				var refreshCycle = $("#ajax_refresh_cycle").text();
				setTimeout(fn.check, refreshCycle * 1000);
			} else {
				$("#message").hide();
				$("#ok_button").show();
			}
		});
	}

	$("#ok_button").on("click", function() {
		if (isEmpty(seat)) {
			var seat = $("#seat").val();
			if (!isEmpty(seat)) {
				fn.ajax("/callStaff", group, seat, fn.check);
				location.href = "/user?group=" + group + "&seat=" + seat;
			}
		} else {
			fn.ajax("/callStaff", group, seat, fn.check);
		}
	});

	$(isEmpty(seat) ? "#seat" : "#ok_button").focus();
	fn.check();
});
