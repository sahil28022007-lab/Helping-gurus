<%-- Admin panel: verification queue with the four authenticity checkboxes and the photo moderation queue. Data comes from AdminServlet. --%><%@ include file="header.jspf" %>
<p class="mut"><b>${campaignCount}</b> campaigns &middot; <b>${userCount}</b> users &middot; <b>${donationCount}</b> donations</p>
<h2>Verification queue</h2><div class="grid">
<c:forEach var="c" items="${queue}"><div class="card pad"><h3><a href="${pageContext.request.contextPath}/campaign?id=${c.id}"><c:out value="${c.title}"/></a></h3>
<div class="mut">by <c:out value="${c.ownerName}"/> &middot; goal ${hg:inr(c.goal)}</div><p><c:out value="${c.story}"/></p><b>Authenticity checks (${c.trustScore}%)</b>
<form method="post" action="${pageContext.request.contextPath}/admin/review"><input type="hidden" name="campaignId" value="${c.id}">
<label class="chk"><input type="checkbox" name="chkId" ${c.chkId ? 'checked' : ''}> Guardian ID proof</label>
<label class="chk"><input type="checkbox" name="chkMedical" ${c.chkMedical ? 'checked' : ''}> Medical report verified</label>
<label class="chk"><input type="checkbox" name="chkHospital" ${c.chkHospital ? 'checked' : ''}> Hospital / doctor letter</label>
<label class="chk"><input type="checkbox" name="chkBank" ${c.chkBank ? 'checked' : ''}> Bank account name matches</label>
<div class="row"><button class="btn g s" name="action" value="save">Save checks</button><button class="btn s" name="action" value="verify">Verify &amp; publish</button><button class="btn o s" name="action" value="reject">Reject</button></div></form></div></c:forEach>
<c:if test="${empty queue}"><p class="mut">Nothing to verify.</p></c:if></div>
<h2>Photo moderation</h2><div class="grid">
<c:forEach var="p" items="${pendingPhotos}"><div class="card pad"><img class="fit" src="${pageContext.request.contextPath}/photo?id=${p.id}" alt="Pending photo"><div class="mut"><c:out value="${p.campaignTitle}"/> &middot; by <c:out value="${p.uploadedBy}"/></div>
<form method="post" action="${pageContext.request.contextPath}/admin/photo" class="row"><input type="hidden" name="photoId" value="${p.id}"><button class="btn s" name="decision" value="approve">Approve</button><button class="btn o s" name="decision" value="reject">Reject</button></form></div></c:forEach>
<c:if test="${empty pendingPhotos}"><p class="mut">No photos waiting.</p></c:if></div>
<%@ include file="footer.jspf" %>
