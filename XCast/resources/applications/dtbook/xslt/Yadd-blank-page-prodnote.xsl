<?xml version="1.0" encoding="UTF-8"?>
<!--<html>
		<p><b>Add-blank-page-prodnote:</b></p>
		<p>Adderar en <b>prodnote</b> till tomma sidor</p>
    </html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="/">
	    <xsl:comment>Add-blank-page-prodnote, version 2005-07-11 15:57</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="pagenum[
		generate-id(following::text()[normalize-space(.)!=''][1])=
		generate-id(following::text()[ancestor::pagenum][1])]">	
		<xsl:call-template name="copy"/>
		<prodnote render="optional">Tom sida</prodnote>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:call-template name="copy"/>
	</xsl:template>

	<xsl:template name="copy">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>

</xsl:stylesheet>
