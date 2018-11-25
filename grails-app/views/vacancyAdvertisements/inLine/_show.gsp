<g:set var="entity" value="${message(code: 'vacancyAdvertisements.entity', default: 'vacancyAdvertisements')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'vacancyAdvertisements')}" />
<g:render template="/vacancyAdvertisements/show" model="[vacancyAdvertisements:vacancyAdvertisements]" />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>