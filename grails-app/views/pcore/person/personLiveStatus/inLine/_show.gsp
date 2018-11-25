<g:render template="/pcore/person/personLiveStatus/show" model="[personLiveStatus:personLiveStatus]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${personLiveStatus?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>