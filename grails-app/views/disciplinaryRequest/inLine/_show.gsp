<g:render template="/disciplinaryRequest/show"
          model="[disciplinaryRequest:disciplinaryRequest]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>