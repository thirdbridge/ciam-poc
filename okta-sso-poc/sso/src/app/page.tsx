import { Suspense } from "react";
import { MainContent } from "@/components/main";
import { Loader } from "@/components/loader/loader";

interface Props {
  params: Promise<{ slug: string }>;
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}

export default async function Home({ searchParams: getSearchParams }: Props) {
  return (
    <Suspense fallback={<Loader />}>
      <MainContent searchParams={getSearchParams} />
    </Suspense>
  );
}
