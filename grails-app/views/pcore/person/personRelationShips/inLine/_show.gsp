<g:render template="/pcore/person/personRelationShips/show" model="[personRelationShips:personRelationShips]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${personRelationShips?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>