<%@ page import="ps.police.common.utils.v1.PCPUtils" %>
%{--<g:render template="/person/wrapper" model="[bean:personLiveStatus?.person]" />--}%
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personLiveStatus?.person,
                                             isDisabled:isPersonDisabled]" />



<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="liveStatus" action="autocomplete" name="liveStatus.id" id ="liveStatusId"
                     label="${message(code:'personLiveStatus.liveStatus.label',default:'liveStatus')}"
                     values="${[[personLiveStatus?.liveStatus?.id,personLiveStatus?.liveStatus?.descriptionInfo?.localName]]}"
                     isDisabled="${personLiveStatus?.id?'true':'false'}"/>
</el:formGroup>
<el:formGroup class="fromDateFormGroup">
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'personLiveStatus.fromDate.label',default:'fromDate')}" value="${personLiveStatus?.fromDate}" />
</el:formGroup>
<el:formGroup class="toDateFormGroup">
    <el:dateField name="toDate"  size="8" class=" " label="${message(code:'personLiveStatus.toDate.label',default:'toDate')}" value="${personLiveStatus?.toDate}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personLiveStatus.note.label',default:'note')}" value="${personLiveStatus?.note}"/>
</el:formGroup>

<script type="text/javascript">
    var dateOfBirth = '${fmt.formatValue([object:personLiveStatus?.person?.dateOfBirth,type:'zoneddate'])}';

    $("#liveStatusId").change(function () {
        var selectedLiveStatus  = $(this).val();
        $(".fromDateFormGroup label").text('${message(code: 'personLiveStatus.fromDate.label')}');
        $(".toDateFormGroup label").text('${message(code: 'personLiveStatus.toDate.label')}');
        gui.formValidatable.removeRequiredField('personLiveStatusForm','toDate');

        if(selectedLiveStatus == '${ps.police.pcore.enums.v1.PersonLiveStatus.DEAD.value()}') {
            $(".toDateFormGroup label").text('${message(code: 'personLiveStatus.dead.toDate.label')}');
            $(".fromDateFormGroup").hide();
            gui.formValidatable.addRequiredField('personLiveStatusForm','toDate');

        }else{
            $(".fromDateFormGroup").show();
        }

    });

</script>