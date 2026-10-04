<%-- Creator dashboard: own campaigns with post-update, invite co-organizer and photo-upload forms, plus the new-campaign form. Data comes from CreatorServlet. --%><%@ include file="header.jspf" %>
<div class="two"><section>
<c:forEach var="c" items="${mine}"><div class="card pad"><span class="pill">${c.status}</span><h3><a href="${pageContext.request.contextPath}/campaign?id=${c.id}"><c:out value="${c.title}"/></a></h3>
<div class="mut">Authenticity ${c.trustScore}% &middot; ${hg:inr(c.raised)} of ${hg:inr(c.goal)}</div><div class="bar"><i style="width:${c.percent}%"></i></div>
<form method="post" action="${pageContext.request.contextPath}/creator/update" class="row"><input type="hidden" name="campaignId" value="${c.id}"><input name="body" placeholder="Post an update…" required><button class="btn s">Post</button></form>
<form method="post" action="${pageContext.request.contextPath}/creator/invite" class="row"><input type="hidden" name="campaignId" value="${c.id}">
<select name="userId"><c:forEach var="x" items="${invitable[c.id]}"><option value="${x.id}"><c:out value="${x.name}"/></option></c:forEach></select><button class="btn g s">Invite co-organizer</button></form>
<form method="post" action="${pageContext.request.contextPath}/creator/photo" enctype="multipart/form-data" class="row"><input type="hidden" name="campaignId" value="${c.id}"><input type="file" name="photo" accept="image/*" required><button class="btn g s">Upload photo</button></form></div></c:forEach>
<c:if test="${empty mine}"><p class="mut">You have no campaigns yet.</p></c:if></section>
<aside><div class="card pad"><h3>Start a campaign</h3><form method="post" action="${pageContext.request.contextPath}/creator/create">
<input name="title" placeholder="Title" required minlength="5"><select name="category"><option>Zolgensma - SMA</option><option>Cancer Fighter</option><option>Other</option></select>
<textarea name="story" rows="5" placeholder="Tell the patient's story…" required></textarea><input type="number" name="goal" min="1" placeholder="Goal (₹)" required><button class="btn full">Submit for verification</button></form></div></aside></div>
<%@ include file="footer.jspf" %>
