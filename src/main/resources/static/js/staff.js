window.addEventListener("load", function() {
	let fn = {};
	fn.ajax = function(url, group, seat, success) {
		let form = new FormData();
		form.append("group", group);
		form.append("seat", seat);
		fetch(url, { method: "POST", body: form })
			.then(response => response.text())
			.then(data => success(data))
			.catch(function() {
				const ajax = () => fn.ajax(url, group, seat, success);
				setTimeout(ajax, 30000);
			});
	}

	let timeout;
	let audio = new Audio("chime.mp3");

	fn.updateCalls = function(isUsingChime) {
		let group = document.getElementById("group").value;
		let seat = "";
		fn.ajax("/getCalls", group, seat, function(callsText) {
			let calls = JSON.parse(callsText);

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

			Array.from(document.querySelectorAll("#calls tr")).filter(e => e.id != "caption").forEach(e => e.remove());
			calls.forEach(function(call) {
				let group = call.groupId;
				let seat = call.seatId;
				let time = format(call.callTime);

				let tr = document.createElement("tr");
				document.getElementById("calls").appendChild(tr);

				let seatTd = document.createElement("td");
				seatTd.innerText = seat
				tr.appendChild(seatTd);

				let timeTd = document.createElement("td");
				timeTd.innerText = time;
				tr.appendChild(timeTd);

				let message = "「" + seat + "」を削除しますか？";
				const updateCalls = () => fn.updateCalls(false);
				const deleteCall = () => fn.ajax("/deleteCall", group, seat, updateCalls);
				const buttonAction = () => confirm(message) && deleteCall();
				let button = document.createElement("button");
				button.innerText = "削除";
				button.onclick = buttonAction;

				let buttonTd = document.createElement("td");
				buttonTd.appendChild(button);
				tr.appendChild(buttonTd);
			});

			clearTimeout(timeout);
			let refreshCycle = document.getElementById("ajax_refresh_cycle").innerText;
			timeout = setTimeout(() => fn.updateCalls(true), refreshCycle * 1000);

			if (isUsingChime && calls.length > 0 && document.getElementById("chime_check").matches(":checked")) {
				audio.play();
			}
		});
	}

	fn.updateCalls(false);
});