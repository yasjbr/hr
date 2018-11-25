<g:render template="/secondmentNotice/show"
          model="[secondmentNotice:secondmentNotice]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>