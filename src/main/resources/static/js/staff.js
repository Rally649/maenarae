window.addEventListener("load", function() {
	function ajax(url, group, seat, success) {
		let form = new FormData();
		form.append("group", group);
		form.append("seat", seat);
		fetch(url, { method: "POST", body: form })
			.then(response => response.text())
			.then(data => success(data))
			.catch(function() {
				const retry = () => ajax(url, group, seat, success);
				setTimeout(retry, 30000);
			});
	}

	let timeout;
	let audio = new Audio("chime.mp3");

	function updateCalls(isUsingChime) {
		let group = document.getElementById("group").value;
		let seat = "";
		ajax("/getCalls", group, seat, function(callsText) {
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

			let tbody = document.querySelector("#calls tbody");
			Array.from(tbody.children).forEach(e => e.remove());
			calls.forEach(function(call) {
				function create(name, attributes) {
					let element = document.createElement(name);
					Object.keys(attributes).forEach(key => element[key] = attributes[key]);
					return element;
				}

				let group = call.groupId;
				let seat = call.seatId;
				let time = format(call.callTime);

				let tr = create("tr", {});
				tbody.appendChild(tr);
				tr.appendChild(create("td", { innerText: seat }));
				tr.appendChild(create("td", { innerText: time }));

				let message = "「" + seat + "」を削除しますか？";
				const deleteCall = () => ajax("/deleteCall", group, seat, () => updateCalls(false));
				const buttonAction = () => confirm(message) && deleteCall();
				let button = create("button", { innerText: "削除", onclick: buttonAction });
				tr.appendChild(create("td", {})).appendChild(button);
			});

			clearTimeout(timeout);
			let refreshCycle = document.getElementById("ajax_refresh_cycle").innerText;
			timeout = setTimeout(() => updateCalls(true), refreshCycle * 1000);

			if (isUsingChime && calls.length > 0 && document.getElementById("chime_check").matches(":checked")) {
				audio.play();
			}
		});
	}

	updateCalls(false);
});