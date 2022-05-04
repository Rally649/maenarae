window.addEventListener("load", function() {
	function setLinks(group) {
		let paths = ["staff", "user"];
		paths.forEach(function(path) {
			let urlObject = new URL(path, location.href);
			urlObject.searchParams.set("group", group.value);
			let url = urlObject.href
			let link = document.getElementById(path + "_link");
			link.setAttribute("href", url);
		});
	}

	function setQrcode(qrcodeArea, url) {
		let width = window.innerWidth;
		let height = window.innerHeight;
		let size = Math.floor(Math.min(width, height) * 0.8);
		new QRCode(qrcodeArea, {
			text: url,
			width: size,
			height: size
		});
	}

	function setModalAction(qrcodeButton, modal) {
		qrcodeButton.onclick = () => modal.style.display = "block";
		modal.onclick = () => modal.style.display = "none";
	}

	function setUrlCopyButton(url, urlButton) {
		const copyUrl = () => navigator.clipboard.writeText(url).then(function() {
			let style = document.getElementById("copied").style;
			style.display = "inline-block";
			setTimeout(() => style.display = "none", 1000);
		});
		urlButton.onclick = copyUrl;
	}

	function getUrl(group) {
		let urlObject = new URL(location.href);
		urlObject.searchParams.set("group", group.value);
		let url = urlObject.href
		return url;
	}

	let group = document.getElementById("group");
	let qrcodeButton = document.getElementById("qrcode_button");
	let urlButton = document.getElementById("url_button");
	let modal = document.getElementById("modal");
	let qrcodeArea = document.getElementById("qrcode");

	let url = getUrl(group);

	setLinks(group);
	setQrcode(qrcodeArea, url);
	setModalAction(qrcodeButton, modal);
	setUrlCopyButton(url, urlButton);
});