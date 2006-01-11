

<xsl:stylesheet version="1.0" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:x="adobe:ns:meta/"
 x:xmptk="XMP toolkit 2.9.1-13, framework 1.6"
 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 xmlns:iX="http://ns.adobe.com/iX/1.0/"
 xmlns:pdf="http://ns.adobe.com/pdf/1.3/"
 xmlns:xap="http://ns.adobe.com/xap/1.0/"
 xmlns:xapMM="http://ns.adobe.com/xap/1.0/mm/"
 xmlns:dc="http://purl.org/dc/elements/1.1/">

<xsl:output method="xml" indent="no"/>

<xsl:template match="x:*|rdf:*|iX:*|pdf:*|xap:*|xapMM:*|dc:*"/>

<xsl:template match="*">
  <xsl:call-template name="copy"/>
</xsl:template>

<xsl:template name="copy">
  <xsl:element name="{name()}">
  	<xsl:for-each select="@*">
  	  <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
  	</xsl:for-each>
  	<xsl:apply-templates/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>