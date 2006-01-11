<?xml version="1.0" encoding="utf-8"?>
<!--
		*** Daisygen ***
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="em[descendant::pagenum]">
		<xsl:variable name="this" select="node()"/>
		Here!
		<xsl:for-each select="descendant::pagenum">
			<xsl:copy-of select="."/>
			<xsl:variable name="id" select="generate-id(.)"/>
			abab
			<xsl:for-each select="$this[generate-id(preceding::pagenum[1])=$id]">
			<unit>
				<xsl:copy-of select="."/>
				</unit>
			</xsl:for-each>
		</xsl:for-each>
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
