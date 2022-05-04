window.addEventListener("load", function() {
	let fn = {};

	let url = new URL(location.href);
	let params = url.searchParams;
	let group = params.get("group");
	let seat = params.get("seat");

	let numberOfWaiting = document.getElementById("number_of_waiting");
	let message = document.getElementById("message");
	let okButton = document.getElementById("ok_button");
	let ajaxRefreshCycle = document.getElementById("ajax_refresh_cycle");

	const isEmpty = (str => str == null || str == '');

	fn.ajax = function(url, group, seat, success) {
		if (!isEmpty(group) && !isEmpty(seat)) {
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
	}

	fn.check = function() {
		fn.ajax("/getNumberOfWaiting", group, seat, function(num) {
			if (num > 0) {
				numberOfWaiting.innerText = num;
				message.style.display = "block";
				okButton.style.display = "none";
				let refreshCycle = ajaxRefreshCycle.innerText;
				setTimeout(fn.check, refreshCycle * 1000);
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
				fn.ajax("/callStaff", group, seat, fn.check);
				location.href = "/user?group=" + group + "&seat=" + seat;
			}
		} else {
			fn.ajax("/callStaff", group, seat, fn.check);
		}
	};

	document.getElementById(isEmpty(seat) ? "seat" : "ok_button").focus();
	fn.check();
});