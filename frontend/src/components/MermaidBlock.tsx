import { useEffect, useRef, useState } from 'react';
import mermaid from 'mermaid';

mermaid.initialize({
  startOnLoad: false,
  theme: 'dark',
  themeVariables: {
    primaryColor: '#6d28d9',
    primaryTextColor: '#e5e7eb',
    primaryBorderColor: '#7c3aed',
    lineColor: '#9ca3af',
    secondaryColor: '#1e3a5f',
    tertiaryColor: '#1f2937',
    background: '#111827',
    mainBkg: '#1f2937',
    nodeBorder: '#4b5563',
    clusterBkg: '#111827',
    clusterBorder: '#374151',
    titleColor: '#f3f4f6',
    edgeLabelBackground: '#1f2937',
  },
  flowchart: { curve: 'basis', padding: 12 },
  fontFamily: 'Manrope, sans-serif',
});

let idCounter = 0;

export default function MermaidBlock({ code }: { code: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const [svg, setSvg] = useState<string>('');
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const id = `mermaid-${idCounter++}`;
    mermaid.render(id, code)
      .then(({ svg }) => {
        setSvg(svg);
        setError('');
      })
      .catch((err) => {
        setError(String(err));
      });
  }, [code]);

  if (error) {
    return (
      <pre className="text-xs text-red-400 bg-gray-900 p-3 rounded overflow-x-auto">
        {code}
      </pre>
    );
  }

  return (
    <div
      ref={ref}
      className="my-4 flex justify-center overflow-x-auto bg-gray-900/50 rounded-lg p-4"
      dangerouslySetInnerHTML={{ __html: svg }}
    />
  );
}
