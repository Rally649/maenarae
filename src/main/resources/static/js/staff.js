$(function() {
	let fn = {};
	fn.ajax = function(url, group, seat, success) {
		$.ajax({
			url: url,
			type: "POST",
			data: { group: group, seat: seat }
		}).done(data => success(data)).fail(function() {
			const ajax = () => fn.ajax(url, group, seat, success);
			setTimeout(ajax, 30000);
		});
	}

	let timeout;
	let audio = new Audio("chime.mp3");

	fn.updateCalls = function(isUsingChime) {
		let group = $("#group").val();
		let seat = "";
		fn.ajax("/getCalls", group, seat, function(calls) {

			const format = function(callTime) {
				const slice = num => ("0" + num).slice(-2);

				let date = new Date(callTime);
				let YYYY = date.getFullYear();
				let MM = slice(date.getMonth() + 1);
				let DD = slice(date.getDate());
				let hh = slice(date.getHours());
				let mm = slice(date.getMinutes());
				let ss = slice(date.getSeconds());
				return YYYY + "-" + MM + "-" + DD + " " + hh + ":" + mm + ":" + ss;
			}

			$("#calls tr").not("#caption").remove();
			calls.forEach(function(call) {
				let group = call.groupId;
				let seat = call.seatId;
				let time = format(call.callTime);

				let tr = $("<tr>").appendTo($("#calls"));
				$("<td>").text(seat).appendTo(tr);
				$("<td>").text(time).addClass("wrap").appendTo(tr);

				let message = "「" + seat + "」を削除しますか？";
				const updateCalls = () => fn.updateCalls(false);
				const deleteCall = () => fn.ajax("deleteCall", group, seat, updateCalls);
				const buttonAction = () => confirm(message) && deleteCall();
				let button = $("<button>").text("削除").on("click", buttonAction);
				button.appendTo($("<td>").appendTo(tr));
			});

			clearTimeout(timeout);
			let refreshCycle = $("#ajax_refresh_cycle").text();
			timeout = setTimeout(() => fn.updateCalls(true), refreshCycle * 1000);

			if (isUsingChime && calls.length > 0 && $("#chime_check").is(":checked")) {
				audio.play();
			}
		});
	}

	fn.updateCalls(false);
});
