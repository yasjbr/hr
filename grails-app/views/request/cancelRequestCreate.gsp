<script type="text/javascript">
    function closeForm() {
        window.location.href = '${createLink(controller:"${request?.requestType?.domain}",action:'list')}';
    }
</script>

<g:set var="entity"
       value="${message(code: 'EnumRequestCategory.'+requestCategory, default: 'Cancel Request')}"/>
<g:set var="formTitle"
       value="${message(code: 'default.create.label', args: [entity], default: 'create')}"/>

<el:validatableModalForm title="${formTitle}"
                         width="70%" callBackFunction="closeForm"
                         name="cancelRequestForm"
                         controller="request"
                         hideCancel="true"
                         hideClose="true"
                         action="saveOperation">
    <msg:modal/>

    <g:render template="/${request?.requestType?.domain}/cancelRequestForm" model="[requestCategory: requestCategory,
                                                                                    request: request, showAllLevels:showAllLevels]" />

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>
