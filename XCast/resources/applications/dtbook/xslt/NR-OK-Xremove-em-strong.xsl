<?xml version="1.0"?>
<!--<html>
		<p><b>Remove-em-strong:</b></p>
		<p>Tar bort alla <b>em</b> eller <b>strong</b> som<br>
		   har en <b>list</b> som förfader.</p>
    </html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="em|strong">
		<xsl:choose>
			<xsl:when test="ancestor::list|ancestor::h1|ancestor::h2|ancestor::h3|ancestor::h4|ancestor::h5|ancestor::h6|ancestor::hd">
				<!--[(parent::level|parent::level1|parent::level2|
    								parent::level3|parent::level4|parent::level5|parent::level6)[@class='toc']]-->
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="copy"/>
			</xsl:otherwise>
		</xsl:choose>
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
