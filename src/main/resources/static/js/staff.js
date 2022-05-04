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

			let tbody = document.querySelector("#calls tbody");
			Array.from(tbody.children).forEach(e => e.remove());
			calls.forEach(function(call) {
				function create(name, attributes) {
					let element = document.createElement(name);
					Object.keys(attributes).forEach(key => element[key] = attributes[key]);
					return element;
				}

				function append(parent, child) {
					parent.appendChild(child);
					return parent;
				}

				let group = call.groupId;
				let seat = call.seatId;
				let time = format(call.callTime);

				let tr = create("tr", {});
				append(tbody, tr)
				append(tr, create("td", { innerText: seat }));
				append(tr, create("td", { innerText: time }));

				let message = "「" + seat + "」を削除しますか？";
				const updateCalls = () => fn.updateCalls(false);
				const deleteCall = () => fn.ajax("/deleteCall", group, seat, updateCalls);
				const buttonAction = () => confirm(message) && deleteCall();
				let button = create("button", { innerText: "削除", onclick: buttonAction });
				append(tr, append(create("td", {}), button));
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