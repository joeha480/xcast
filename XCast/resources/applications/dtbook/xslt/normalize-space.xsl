<?xml version="1.0" encoding="UTF-8"?>
<!--
		Tar bort extra whitespace mellan noder, men 
		bevarar ett whitespace på varje sida om noder
		som förekommer i mixed content. T.ex.:
		<em> emphasis </em> 
		ändras inte.
-->
<!-- 
		Version 2005-11-28
		Joel Håkansson, TPB
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="yes" method="xml" encoding="UTF-8"/>

<xsl:template match="text()">
	<xsl:variable name="string" select="normalize-space(.)"/>
	<xsl:if test="string-length($string)&gt;0">
		<xsl:if test="substring-before(., $string)"><xsl:text> </xsl:text></xsl:if>
		<xsl:value-of select="$string"/>
		<xsl:if test="substring-after(., $string)"><xsl:text> </xsl:text></xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template match="*|comment()|processing-instruction()">
	<xsl:call-template name="copy"/>
</xsl:template>

<xsl:template name="copy">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>
