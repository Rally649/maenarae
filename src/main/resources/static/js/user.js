window.addEventListener("load", function() {
	let url = new URL(location.href);
	let params = url.searchParams;
	let group = params.get("group");
	let seat = params.get("seat");

	let numberOfWaiting = document.getElementById("number_of_waiting");
	let message = document.getElementById("message");
	let okButton = document.getElementById("ok_button");
	let ajaxRefreshCycle = document.getElementById("ajax_refresh_cycle");

	const isEmpty = (str => str == null || str == '');

	function ajax(url, group, seat, success) {
		if (!isEmpty(group) && !isEmpty(seat)) {
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
	}

	function check() {
		ajax("/getNumberOfWaiting", group, seat, function(num) {
			if (num > 0) {
				numberOfWaiting.innerText = num;
				message.style.display = "block";
				okButton.style.display = "none";
				let refreshCycle = ajaxRefreshCycle.innerText;
				setTimeout(check, refreshCycle * 1000);
			} else {
				message.style.display = "none";
				okButton.style.display = "block";
			}
		});
	}

	okButton.onclick = function() {
		if (isEmpty(seat)) {
			let seat = document.getElementById("seat").value;
			if (!isEmpty(seat)) {
				ajax("/callStaff", group, seat, check);
				location.href = "/user?group=" + group + "&seat=" + seat;
			}
		} else {
			ajax("/callStaff", group, seat, check);
		}
	};

	document.getElementById(isEmpty(seat) ? "seat" : "ok_button").focus();
	check();
});