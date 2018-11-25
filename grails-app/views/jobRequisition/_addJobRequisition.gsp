<el:dataTable id="jobRequisitionTableToChoose"
              searchFormName="jobRequisitionSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="jobRequisition"
              spaceBefore="true"
              hasRow="true"
              action="filterJobRequisitionToAdd"
              domainColumns="DOMAIN_COLUMNS_FOR_RECRUITMENT_CYCLE"
              serviceName="jobRequisition">
</el:dataTable>