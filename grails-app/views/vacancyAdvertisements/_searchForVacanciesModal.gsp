<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="recruitmentCycle"
                     action="autocomplete"
                     name="recruitmentCycle.id"
                     label="${message(code: 'vacancy.recruitmentCycle.label', default: 'recruitmentCycle')}"/>

    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="job"
                     action="autocomplete"
                     name="job.id"
                     label="${message(code: 'vacancy.job.label', default: 'job')}"/>
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfPositions"
                     size="6"
                     class=" isNumber"
                     label="${message(code: 'vacancy.numberOfPositions.label', default: 'numberOfPositions')}"/>
</el:formGroup>
%{--
<el:hiddenField name="vacancyStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.NEW}"/>
--}%
<el:formGroup id="execluded-vacancies">

</el:formGroup>

<el:hiddenField name="recruitmentCycle.id" id="recruitmentCycleId" value=""/>
<el:hiddenField name="filterForVacancyAdvertisement" value="true"/>

<script>
    $(document).ready(function () {
        $('#vacancyTable1 > tbody  > tr > td > input[name="vacancy"]').each(function () {
            $("#execluded-vacancies").append("<input class='form-control' id='execludedIds' name='execludedIds' value='" + $(this).val() + "' type='hidden'>");
        });

        $("#recruitmentCycleId").val($("#recruitmentCycle").val())
    });
</script>