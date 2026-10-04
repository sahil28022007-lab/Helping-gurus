<%-- Single campaign page: progress, organizers, approved photos, updates, comments and the donate form. Data comes from CampaignServlet. --%><%@ include file="header.jspf" %>
<div class="two">
<section>
<div class="card wide"><div class="cover ${fn:contains(c.category,'SMA') ? 'a' : 'b'} big"><span class="badge ${c.status == 'LIVE' ? '' : 'w'}">${c.status == 'LIVE' ? '&#10004; Verified' : c.status}</span></div>
<div class="pad"><span class="pill"><c:out value="${c.category}"/></span><h2><c:out value="${c.title}"/></h2>
<div class="mut">Organizers: <c:forEach var="n" items="${team}" varStatus="s"><c:out value="${n}"/>${s.last ? '' : ', '}</c:forEach> &middot; Authenticity ${c.trustScore}%</div>
<div class="bar"><i style="width:${c.percent}%"></i></div><div class="mut"><b>${hg:inr(c.raised)}</b> of ${hg:inr(c.goal)} (${c.percent}%)</div>
<p><c:out value="${c.story}"/></p>
<c:if test="${not empty photos}"><div class="gal"><c:forEach var="p" items="${photos}"><img src="${pageContext.request.contextPath}/photo?id=${p.id}" alt="Campaign photo"></c:forEach></div></c:if></div></div>
<div class="card wide pad"><h3>Updates</h3>
<c:forEach var="u" items="${updates}"><p class="mut">&#128204; <b><c:out value="${u.author}"/></b>: <c:out value="${u.body}"/></p></c:forEach><c:if test="${empty updates}"><p class="mut">No updates yet.</p></c:if>
<h3>Comments</h3>
<c:forEach var="m" items="${comments}"><p class="mut"><b><c:out value="${m.author}"/>:</b> <c:out value="${m.body}"/></p></c:forEach>
<c:if test="${not empty user}"><form method="post" action="${pageContext.request.contextPath}/comment" class="row"><input type="hidden" name="campaignId" value="${c.id}"><input name="body" placeholder="Write a comment…" required maxlength="500"><button class="btn">Post</button></form></c:if></div>
</section>
<aside>
<div class="card pad"><h3>Contribute</h3>
<c:choose>
<c:when test="${canDonate}"><form method="post" action="${pageContext.request.contextPath}/donate"><input type="hidden" name="campaignId" value="${c.id}">
<input type="number" name="amount" min="10" step="1" placeholder="Amount (₹)" required><input name="message" placeholder="Message of support (optional)" maxlength="250">
<label class="mut"><input type="checkbox" name="anonymous"> Give anonymously</label><button class="btn o full">Contribute</button></form></c:when>
<c:when test="${empty user}"><p class="mut"><a href="${pageContext.request.contextPath}/login">Log in</a> to contribute.</p></c:when>
<c:otherwise><p class="mut">Contributions are not available for this account or campaign.</p></c:otherwise></c:choose></div>
<div class="card pad"><h3>Wall of support</h3>
<c:forEach var="d" items="${donations}"><p class="mut">&#128154; <b><c:out value="${d.donorName}"/></b> gave ${hg:inr(d.amount)} <c:out value="${d.message}"/></p></c:forEach>
<c:if test="${empty donations}"><p class="mut">Be the first to give.</p></c:if></div></aside></div>
<%@ include file="footer.jspf" %>
