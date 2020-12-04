<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<!DOCTYPE html><html lang=zh-CN><head><meta charset=utf-8><meta http-equiv=X-UA-Compatible content="IE=edge"><title>${orgName}</title><script>
			var SYS_ORG_ID = "${orgId}";
			var SYS_ORG_NAME = "${orgName}";
			var SYS_ORG_DNS = "${dns}";
			
	document.addEventListener('DOMContentLoaded', function() {
				document.documentElement.style.fontSize = document.documentElement.clientWidth / 20 + 'px'
			})
			var coverSupport = 'CSS' in window && typeof CSS.supports === 'function' && (CSS.supports('top: env(a)') || CSS.supports(
				'top: constant(a)'))
			document.write(
				'<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0' +
				(coverSupport ? ', viewport-fit=cover' : '') + '" />')
		</script>
		<link rel=stylesheet href=/wx/static/index.css> <link href=/wx/static/js/pages-index-index.9adda095.js rel=prefetch>
		<link href=/wx/static/js/pages-login-login.ef779b46.js rel=prefetch>
		<link href=/wx/static/js/pages-payment-pay-payment-pay.afc7602b.js rel=prefetch>
		<link href=/wx/static/js/pages-payment-payment.1fa8fa01.js rel=prefetch>
		<link href=/wx/static/js/chunk-vendors.225d5904.js rel=preload as=script>
		<link href=/wx/static/js/index.e0688a9f.js rel=preload as=script>
	</head>
	<body><noscript><strong>Please enable JavaScript to continue.</strong></noscript>
		<div id=app></div>
		<script src=/wx/static/js/chunk-vendors.225d5904.js> </script> <script src=/wx/static/js/index.e0688a9f.js> </script>
		 </body> </html>